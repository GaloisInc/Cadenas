package com.galois.cadenas.crypto

/**
 * Functions to complete the decryption of a ciphertext payload.
 *
 * Such functions are defined in the context of a [SymmetricCryptoSystem], as
 * part of [SymmetricCryptoSystem.beginDecrypt]: Given a ciphertext payload,
 * the first [SymmetricCryptoSystem.numBytesToCheck] bytes are used to quickly
 * verify that a ciphertext is legitimate. If so, a `FinishDecryption` is
 * returned that takes the remaining payload bytes and attempts to complete
 * decryption.
 *
 * In (pseudo)code, for a given key and plaintext, the function must be usable
 * as:
 *
 * ```kotlin
 * val enc = SymmetricCryptoSystem(key)
 * val payload = enc.encrypt(plaintext)
 * // ...
 * val finishDecrypt = beginDecrypt(payload.sliceArray(0 until enc.numBytesToCheck))
 * val decryptedPlaintext = if (finishDecrypt != null) {
 *     finishDecrypt(payload.sliceArray(enc.numBytesToCheck until payload.size))
 * } else {
 *     // ... Decryption failed ...
 * }
 * ```
 */
typealias FinishDecryption = (ByteArray) -> ByteArray?

/**
 * Authenticated symmetric-key crypto-systems with fast-check decryption.
 *
 * This interface describes symmetric-key crypto-systems where decryption may
 * be able to exit early based on a prefix of the payload. Implementations must
 * be _authenticated_ schemes to prevent forgery.
 *
 * This early-exit is facilitated by [beginDecrypt], which checks the payload
 * prefix, returning `null` if the payload is determined to be invalid and a
 * function to decrypt the remaining payload bytes. This allows for systems
 * without a prefix-checking mechanism to be implemented against this same
 * interface.
 *
 * Implementations of this interface should be thread-safe.
 */
interface SymmetricCryptoSystem {
    /** The number of bytes of ciphertext needed to detect (in)validity early. */
    val numBytesToCheck: Int

    /**
     * Encrypt a [plaintext].
     *
     * @param plaintext The plaintext bytes to encrypt using this
     * [SymmetricCryptoSystem].
     *
     * @return a ciphertext payload.
     */
    fun encrypt(plaintext: ByteArray): ByteArray

    /**
     * For a ciphertext payload prefixed by a valid [bytesToCheck], return a
     * [FinishDecryption] that decrypts the remaining bytes of the payload.
     *
     * @param bytesToCheck The first [numBytesToCheck] of a ciphertext payload.
     * `bytesToCheck.size` _must_ be equal to `numBytesToCheck`.
     *
     * @return a [FinishDecryption] to finish decrypting the ciphertext
     * payload, or null if [bytesToCheck] was invalid.
     */
    fun beginDecrypt(bytesToCheck: ByteArray): FinishDecryption?
}