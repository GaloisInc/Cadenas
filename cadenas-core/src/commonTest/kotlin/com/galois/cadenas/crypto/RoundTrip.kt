package com.galois.cadenas.crypto

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.byteArray
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll

private fun roundTripProperty(
    scs: SymmetricCryptoSystem,
    plaintext: ByteArray,
): Boolean {
    val res = scs.encrypt(plaintext)
    val finishDecrypt = scs.beginDecrypt(res.sliceArray(0 until scs.numBytesToCheck)) ?: return false

    val decrypted = finishDecrypt(res.sliceArray(scs.numBytesToCheck until res.size))
    return plaintext.contentEquals(decrypted)
}

/**
 * Round-trip tests for [SymmetricCryptoSystem]s.
 *
 * For library maintainers: After implementing a new `SymmetricCryptoSystem`,
 * make sure to add a test for it here!
 */
class RoundTrip : StringSpec({
    "SivChaChaWithSentinel" {
        val keyArb = Arb.byteArray(Arb.constant(32), Arb.byte())
        val plaintextArb = Arb.byteArray(Arb.int(0, 32), Arb.byte())

        forAll(keyArb, plaintextArb) { key, plaintext ->
            roundTripProperty(SivChaChaWithSentinel(key), plaintext)
        }
    }

    "SivAesWithSentinel" {
        val keyArb = Arb.byteArray(Arb.constant(32), Arb.byte())
        val plaintextArb = Arb.byteArray(Arb.int(0, 32), Arb.byte())

        forAll(keyArb, plaintextArb) { key, plaintext ->
            roundTripProperty(SivAesWithSentinel(key), plaintext)
        }
    }
})