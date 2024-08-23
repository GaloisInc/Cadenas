package com.galois.cadenas.crypto

import com.google.crypto.tink.aead.internal.InsecureNonceXChaCha20
import com.google.crypto.tink.subtle.Hkdf
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * A deterministic symmetric-key encryption scheme, defined as follows:
 *
 * ```
 * Encrypt(K1, K2, K3, M):
 *   MAC <- PRF_{K1}(M)          // 16 bytes
 *   MAC[0], MAC[1], MAC[2] <- 0 // Set sentinel bytes
 *   SV <- AES_{K2}(MAC)         // AES (block) encrypt
 *   N <- MAC || [0; 8]          // Pad to 24 bytes
 *   C <- XCHACHA20_{K3}(N, M)   // XChaCha20 encrypt
 *   Return(SV, C)
 *
 * Decrypt(K1, K2, K3, SV, C):
 *   DSV <- AES_{K2}(SV)         // AES (block) decrypt
 *   If DSV[0], DSV[1], DSV[2] are not all 0 then abort
 *   N <- DSV[0..16] || [0; 8]   // Pad to 24 bytes
 *   M <- XCHACHA20_{K3}(N, C)   // XChaCha20 decrypt
 *   MAC <- PRF_{K1}(M)          // Recompute MAC
 *   MAC[0], MAC[1], MAC[2] <- 0 // Set sentinel bytes
 *   If DSV != MAC then abort
 *   Return M
 * ```
 *
 * The keys `K1`, `K2`, and `K3` are derived from a given key `K`.
 *
 * Note that the security of this system is very sensitive - in particular, the
 * number of bytes of entropy in the nonce `N` must be kept sufficiently large
 * to keep the probability of encrypting different messages with the same nonce
 * small.
 *
 * In particular, the expected number of nonce values that can be generated
 * before colliding with probability `p` is approximately:
 *
 * `sqrt(2 * 2^(8 * MAC_SIZE - SENTINEL_SIZE) * ln(1 / (1 - p)))`
 */
class SivChaChaWithSentinel(
    key: ByteArray,
) : SymmetricCryptoSystem {
    override val numBytesToCheck = MAC_SIZE

    private val hmacKey: ByteArray

    private val sentinelEncrypt: Cipher
    private val sentinelDecrypt: Cipher

    private val chachaKey: ByteArray
    private val chachaEnc: InsecureNonceXChaCha20

    init {
        require(key.size == KEY_SIZE)

        val hkdf = Hkdf.computeHkdf("HMACSHA512", key, ByteArray(0), "August 29, 2023 Cadenas cryptography".toByteArray(), 3 * KEY_SIZE)

        hmacKey = hkdf.sliceArray(0 until KEY_SIZE)

        val sentinelKey = hkdf.sliceArray(KEY_SIZE until 2 * KEY_SIZE)
        sentinelEncrypt = initAes(sentinelKey, isDecryptMode = false)
        sentinelDecrypt = initAes(sentinelKey, isDecryptMode = true)

        chachaKey = hkdf.sliceArray(2 * KEY_SIZE until hkdf.size)
        chachaEnc = InsecureNonceXChaCha20(chachaKey, 0)
    }

    private fun initAes(key: ByteArray, isDecryptMode: Boolean): Cipher {
        val cipher = Cipher.getInstance("AES/ECB/NoPadding")

        val keySpec = SecretKeySpec(key, "AES")
        if (isDecryptMode) {
            cipher.init(Cipher.DECRYPT_MODE, keySpec)
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        }

        return cipher
    }

    override fun encrypt(plaintext: ByteArray): ByteArray {
        val hmac = initHmac(hmacKey)
        hmac.update(plaintext)
        val mac = hmac.doFinal().sliceArray(0 until MAC_SIZE)

        for (i in 0 until SENTINEL_SIZE) {
            mac[i] = 0
        }
        val sentinel = sentinelEncrypt.doFinal(mac)

        val nonce = ByteArray(NONCE_SIZE)
        mac.copyInto(nonce, 0, 0, MAC_SIZE)

        return sentinel + chachaEnc.encrypt(nonce, plaintext)
    }

    private fun initHmac(key: ByteArray): Mac {
        val algorithm = "HmacSHA256"

        val mac = Mac.getInstance(algorithm)

        mac.init(SecretKeySpec(key, algorithm))

        return mac
    }

    override fun beginDecrypt(bytesToCheck: ByteArray): FinishDecryption? {
        require(bytesToCheck.size == numBytesToCheck)

        val decryptedSentinel = sentinelDecrypt.doFinal(bytesToCheck)

        if (!MessageDigest.isEqual(ByteArray(SENTINEL_SIZE), decryptedSentinel.sliceArray(0 until SENTINEL_SIZE))) {
            return null
        }

        return { ciphertext: ByteArray ->
            val nonce = ByteArray(NONCE_SIZE)

            decryptedSentinel.sliceArray(0 until MAC_SIZE).copyInto(nonce, 0, 0, MAC_SIZE)

            val plaintext = chachaEnc.decrypt(nonce, ciphertext)

            val hmac = initHmac(hmacKey)
            hmac.update(plaintext)
            val mac = hmac.doFinal().sliceArray(0 until MAC_SIZE)
            for (i in 0 until SENTINEL_SIZE) {
                mac[i] = 0
            }

            if (!MessageDigest.isEqual(mac, decryptedSentinel)) {
                null
            } else {
                plaintext
            }
        }
    }

    private companion object {
        const val KEY_SIZE: Int = 32

        const val MAC_SIZE: Int = 16

        const val SENTINEL_SIZE: Int = 3

        const val NONCE_SIZE: Int = 24
    }
}