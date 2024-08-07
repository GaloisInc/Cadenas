package com.galois.cadenas.crypto

import com.google.crypto.tink.subtle.Hkdf
import java.math.BigInteger
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.xor

fun ByteArray.xor(other: ByteArray): ByteArray {
    return this.zip(other) { b1, b2 -> b1.xor(b2) }.toByteArray()
}

/**
 * A deterministic symmetric key encryption scheme, defined as follows:
 *
 * ```
 * Encrypt(K1, K2, SV, M):
 *   N <- PRF_{K1}(SV || M)
 *   C <- CTR[AES_{K2}](N, SV || M)      // AES encrypt
 *   Return(N, C)
 *
 * Decrypt(K1, K2, N, C):
 *   SV, M <- CTR[AES_{K2}](N, C)        // AES decrypt
 *   If SV is invalid then abort
 *   If N != PRF_{K1}(SV || M) then abort
 *   Return M
 * ```
 *
 * `N` denotes the nonce, `SV` denotes the sentinel, and the keys `K1` and `K2`
 * are derived from a given key `K`. This implementation sets `SV` to all zeros.
 *
 * @property key The secret key. This _must_ be 32 bytes long.
 * @property numNonceBytes The number of bytes of nonce to use. CAUTION:
 * Setting this lower than the default 16 bytes results in a significant loss
 * of security!
 * @property numSentinelBytes The number of bytes of sentinel to use. These
 * bytes are used to implement the quick check of [beginDecrypt] - in the
 * context of MB-FTE, the larger this value, the fewer false positives (that
 * is, messages that one thinks can be decoded but actually cannot.)
 */
class SivAesWithSentinel(
    private val key: ByteArray,
    private val numNonceBytes: Int = 16,
    private val numSentinelBytes: Int = 2,
) : SymmetricCryptoSystem {
    /**
     * For this crypto-system, we check a number of bytes equal to the number
     * of nonce and sentinel bytes.
     */
    override val numBytesToCheck = numNonceBytes + numSentinelBytes

    private val nonceKey: ByteArray
    private val encryptionKey: ByteArray

    private val aesEncryptor: Cipher

    init {
        require(key.size == KEY_SIZE)
        require(numSentinelBytes <= BLOCK_SIZE)
        require(numNonceBytes <= BLOCK_SIZE)

        val hkdf = Hkdf.computeHkdf(
            "HMACSHA512",
            key,
            ByteArray(0),
            "August 29, 2023 Cadenas cryptography".toByteArray(),
            2 * KEY_SIZE
        )
        nonceKey = hkdf.sliceArray(0 until KEY_SIZE)
        encryptionKey = hkdf.sliceArray(KEY_SIZE until hkdf.size)

        aesEncryptor = Cipher.getInstance("AES/ECB/NoPadding")
        aesEncryptor.init(Cipher.ENCRYPT_MODE, SecretKeySpec(encryptionKey, "AES"))
    }

    /**
     * Encrypt [plaintext] using the [SivAesWithSentinel] crypto-system.
     *
     * @param plaintext The plaintext bytes to encrypt.
     *
     * @return a ciphertext payload; [numNonceBytes] of nonce followed by the
     * 'actual' payload.
     */
    override fun encrypt(plaintext: ByteArray): ByteArray {
        val sentinel = ByteArray(numSentinelBytes)

        val expandedPlaintext = sentinel + plaintext

        // Compute N <- PRF_{K1}(SV || M)
        val hmac = initHMAC(nonceKey)
        hmac.update(expandedPlaintext)
        val nonce = hmac.doFinal().sliceArray(0 until numNonceBytes)

        // Compute C <- CTR[AES_{K2}](N, SV || M)
        // Manual CTR mode to control nonce
        // NOTE: We should consider using the approach here to avoid the manual counter
        // logic: https://github.com/google/tink/blob/master/java_src/src/main/java/com/google/crypto/tink/subtle/AesCtrJceCipher.java
        var ciphertext = ByteArray(0)
        expandedPlaintext.asSequence().chunked(BLOCK_SIZE).forEachIndexed { i, block ->
            val fullNonce = fullNonce(nonce, i)
            val encryptedBlock = aesEncryptor.doFinal(fullNonce)
            val ciphertextBlock = block.toByteArray().xor(encryptedBlock)
            ciphertext += ciphertextBlock
        }

        return nonce + ciphertext
    }

    private fun initHMAC(key: ByteArray): Mac {
        val algorithm = "HmacSHA256"

        val secretKeySpec = SecretKeySpec(key, algorithm)
        val mac = Mac.getInstance(algorithm)

        mac.init(secretKeySpec)

        return mac
    }

    private fun fullNonce(nonce: ByteArray, increment: Int): ByteArray {
        require(nonce.size == numNonceBytes)

        val paddedNonce = nonce + ByteArray(BLOCK_SIZE - numNonceBytes)
        val incremented = BigInteger(paddedNonce).add(increment.toBigInteger()).toByteArray()

        return if (incremented.size < BLOCK_SIZE) {
            ByteArray(BLOCK_SIZE - incremented.size) + incremented
        } else {
            incremented.sliceArray(0 until BLOCK_SIZE)
        }
    }

    /**
     * Begin decryption of a payload using [SivAesWithSentinel] by checking the
     * encrypted sentinel.
     *
     * @param bytesToCheck The first [numBytesToCheck] bytes of a ciphertext
     * payload (i.e. the nonce and encrypted sentinel)
     *
     * @return a function taking the remaining payload bytes if the check
     * succeeds; `null` otherwise. The returned function should return `null`
     * if decryption fails.
     */
    override fun beginDecrypt(bytesToCheck: ByteArray): FinishDecryption? {
        require(bytesToCheck.size == numBytesToCheck)

        // Compute SV <- CTR[AES_{K2}](N, encrypted-SV)
        val nonce = bytesToCheck.sliceArray(0 until numNonceBytes)
        val sentinel =
            bytesToCheck.sliceArray(numNonceBytes until numBytesToCheck) + ByteArray(BLOCK_SIZE - numSentinelBytes)
        val decryptedSentinel = aesEncryptor.doFinal(fullNonce(nonce, 0)).xor(sentinel)

        if (!MessageDigest.isEqual(ByteArray(numSentinelBytes), decryptedSentinel.sliceArray(0 until numSentinelBytes))) {
            return null
        }

        return { ciphertext: ByteArray ->
            // To satisfy the call invariant defined by SymmetricCryptoSystem,
            // we add back the bytes of the encrypted sentinel, then decrypt
            val ciphertextWithSentinel =
                bytesToCheck.sliceArray(numNonceBytes until numBytesToCheck) + ciphertext

            // Compute SV, M <- CTR[AES_{K2}](N, C)
            var plaintext = ByteArray(0)
            ciphertextWithSentinel.asSequence().chunked(BLOCK_SIZE).forEachIndexed { i, block ->
                val fullNonce = fullNonce(nonce, i)
                val encryptedBlock = aesEncryptor.doFinal(fullNonce)
                plaintext += block.toByteArray().xor(encryptedBlock)
            }

            // Check that the nonce is correct
            val hmac = initHMAC(nonceKey)
            hmac.update(plaintext)
            val recomputedNonce = hmac.doFinal().sliceArray(0 until numNonceBytes)
            if (!MessageDigest.isEqual(nonce, recomputedNonce)) {
                null
            } else {
                plaintext.sliceArray(numSentinelBytes until plaintext.size)
            }
        }
    }

    private companion object {
        /** The AES block size (in bytes) used in this crypto-system. */
        const val BLOCK_SIZE: Int = 16

        /** The key size required by this crypto-system. */
        const val KEY_SIZE: Int = 32
    }
}