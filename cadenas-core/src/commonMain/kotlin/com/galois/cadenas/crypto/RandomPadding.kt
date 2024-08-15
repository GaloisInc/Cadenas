package com.galois.cadenas.crypto

import java.security.SecureRandom

/**
 * Turn deterministic crypto-systems into non-deterministic ones with random
 * padding bytes.
 *
 * This class acts as a decorator for [SymmetricCryptoSystem]s to introduce
 * non-determinism via the addition of a given number of random bytes to the
 * plaintext before encrypting with the underlying system.
 *
 * @property cryptoSystem The underlying [SymmetricCryptoSystem].
 * @property numPaddingBytes The number of random bytes to add. Increase this
 * to decrease the probability that, for a fixed plaintext, we produce the same
 * ciphertext. Specifically, the probability that for a fixed plaintext the
 * padded plaintext is equivalent is `1 / 2^(8 * numPaddingBytes)`.
 */
class RandomPadding(
    private val cryptoSystem: SymmetricCryptoSystem,
    private val numPaddingBytes: Int = 6,
) : SymmetricCryptoSystem by cryptoSystem {
    // NOTE: SHA1 is insecure, but we are not relying on it for any security
    // here; it is just a source of randomness.
    private val rng = SecureRandom.getInstance("SHA1PRNG")

    /**
     * Prepend [numPaddingBytes] random bytes to [plaintext], and encrypt with
     * [cryptoSystem].
     */
    override fun encrypt(plaintext: ByteArray): ByteArray {
        val padding = ByteArray(numPaddingBytes)
        rng.nextBytes(padding)

        return cryptoSystem.encrypt(padding + plaintext)
    }

    /**
     * Using [cryptoSystem], validate [bytesToCheck]. Return a
     * [FinishDecryption] that decrypts the remaining ciphertext bytes using
     * [cryptoSystem] and removes the [numPaddingBytes] random bytes.
     */
    override fun beginDecrypt(bytesToCheck: ByteArray): FinishDecryption? {
        val finishDecrypt = cryptoSystem.beginDecrypt(bytesToCheck) ?: return null

        return { ciphertext: ByteArray ->
            val paddedPlaintext = finishDecrypt(ciphertext)
            paddedPlaintext?.sliceArray(numPaddingBytes until paddedPlaintext.size)
        }
    }
}