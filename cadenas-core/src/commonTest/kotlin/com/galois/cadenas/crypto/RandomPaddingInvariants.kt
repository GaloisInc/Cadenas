package com.galois.cadenas.crypto

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.byteArray
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

private class IdentityCryptoSystem : SymmetricCryptoSystem {
    override val numBytesToCheck = 0

    override fun encrypt(plaintext: ByteArray) = plaintext

    override fun beginDecrypt(bytesToCheck: ByteArray): FinishDecryption = { it }
}

/**
 * Invariants of the [RandomPadding] decorator.
 */
class RandomPaddingInvariants : StringSpec({
    "LengthsCorrect" {
        val plaintextArb = Arb.byteArray(Arb.int(0, 32), Arb.byte())
        val paddingArb = Arb.int(0, 32)

        checkAll(plaintextArb, paddingArb) { plaintext, numPaddingBytes ->
            val cryptoSystem =
                RandomPadding(IdentityCryptoSystem(), numPaddingBytes = numPaddingBytes)
            val ciphertext = cryptoSystem.encrypt(plaintext)

            ciphertext.size shouldBe plaintext.size + numPaddingBytes

            val finishDecrypt = cryptoSystem.beginDecrypt(ByteArray(0))!!
            val decrypted = finishDecrypt(ciphertext)!!

            decrypted.size shouldBe ciphertext.size - numPaddingBytes
        }
    }
})