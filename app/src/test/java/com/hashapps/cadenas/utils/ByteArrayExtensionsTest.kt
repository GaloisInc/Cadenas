package com.hashapps.cadenas.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.byteArray
import io.kotest.property.arbitrary.constant
import io.kotest.property.checkAll

/**
 * Unit and round-trip tests for the extensions added to String and ByteArray
 * to handle hex strings.
 */
class ByteArrayExtensionsTest : StringSpec({
    "Zero string to byte" {
        "00".toHexBytes() shouldBe ByteArray(1)
    }

    "Zero byte to string" {
        byteArrayOf(0).toHex() shouldBe "00"
    }

    "Two zeros to bytes" {
        "0000".toHexBytes() shouldBe ByteArray(2)
    }

    "Two zero bytes to string" {
        byteArrayOf(0, 0).toHex() shouldBe "0000"
    }

    "0001 to bytes" {
        "0001".toHexBytes() shouldBe byteArrayOf(0, 1)
    }

    "Zero and one bytes to string" {
        byteArrayOf(0, 1).toHex() shouldBe "0001"
    }

    "Round-trip property, 32 bytes" {
        val bytesArb = Arb.byteArray(Arb.constant(32), Arb.byte())
        checkAll(bytesArb) {
            it.toHex().toHexBytes() shouldBe it
        }
    }
})