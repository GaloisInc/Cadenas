package com.galois.cadenas.utils

import java.math.BigInteger

/**
 * A class the represents a *mutable* 32-bit integer.
 *
 * The class supports the standard numerical and comparison operations
 * supported by the [UInt] class and additionally provides support to
 * get and set individual bits on the number.
 */
class MutableUInt32(initial: UInt): Number(), Comparable<MutableUInt32> {

    constructor(): this(0.toUInt())

    constructor(initial: Number): this(initial.toInt().toUInt())

    /** Store for the actual unsigned 32-bit value */
    private var value: UInt = initial

    /**
     * Get the bit at the given [index].
     *
     * The least significant bit is index 0 and the most
     * significant bit is 31. Note that we deliberately do not
     * support negative indexing a la Python since that is uncommon
     * in Kotlin.
     *
     * @param index The index of the bit with LSB = 0 and MSB = 31
     * @return The value of the bit at [index]
     */
    operator fun get(index: Int): MutableUInt32 {
        val res = (value shr index) and 1u
        return MutableUInt32(res)
    }

    /**
     * Set the bit at the given [index].
     *
     * The least significant bit is index 0 and the most
     * significant bit is 31. Note that we deliberately do not
     * support negative indexing a la Python since that is uncommon
     * in Kotlin.
     *
     * @param index The index of the bit with LSB = 0 and MSB = 31
     * @param newValue The new value for the bit. Must be either 0 or 1
     */
    operator fun set(index: Int, newValue: Number) {
        val asUInt = newValue.toInt().toUInt()
        require(asUInt == 0u || asUInt == 1u)

        val mask = 1u shl index
        value = if (asUInt == 1u) value or mask else value and mask.inv()
    }


    /**
     * Set the bit at the given [index].
     *
     * The least significant bit is index 0 and the most
     * significant bit is 31. Note that we deliberately do not
     * support negative indexing a la Python since that is uncommon
     * in Kotlin.
     *
     * This method is necessary because [UInt] is not an instance of
     * [Number] and we would like to support it on the rhs of the set
     * operation.
     *
     * @param index The index of the bit with LSB = 0 and MSB = 31
     * @param newValue The new value for the bit. Must be either 0 or 1
     */
    operator fun set(index: Int, newValue: UInt) {
        set(index, newValue.toInt())
    }

    /**
     * Converts this value to a [Byte].
     */
    override fun toByte(): Byte {
        return value.toByte()
    }

    /**
     * Converts this value to a [Double].
     */
    override fun toDouble(): Double {
        return value.toDouble()
    }

    /**
     * Converts this value to a [Float].
     */
    override fun toFloat(): Float {
        return value.toFloat()
    }

    /**
     * Converts this value to a [Int].
     */
    override fun toInt(): Int {
        return value.toInt()
    }

    /**
     * Converts this value to a [Long].
     */
    override fun toLong(): Long {
        return value.toLong()
    }

    /**
     * Converts this value to a [Short].
     */
    override fun toShort(): Short {
        return value.toShort()
    }

    /**
     * Converts this value to a [UInt].
     */
    fun toUInt(): UInt {
        return value
    }

    /**
     * Converts this value to a [BigInteger].
     */
    fun toBigInteger(): BigInteger {
        return BigInteger(value.toString())
    }

    /**
     * Converts this value to an array of bytes in the big-endian order.
     */
    fun toBytes(): ByteArray {
        return this.toBigInteger().toByteArray()
    }

    /**
     * Converts this value to a string of bits.
     */
    fun toBitString(): String {
        return toBytes().joinToString { it.toUInt().toString(radix =  2) }
    }

    /**
     * Adds the other value to this value.
     */
    operator fun plus(other: MutableUInt32): MutableUInt32 {
        return MutableUInt32(value + other.value)
    }

    /**
     * Subtract the other value from this value.
     */
    operator fun minus(other: MutableUInt32): MutableUInt32 {
        return MutableUInt32(value - other.value)
    }

    /**
     * Multiply the other value with this value.
     */
    operator fun times(other: MutableUInt32): MutableUInt32 {
        return MutableUInt32(value * other.value)
    }

    /**
     * Divide this value by the other value.
     */
    operator fun div(other: MutableUInt32): Double {
        return value.toDouble() / other.value.toDouble()
    }

    /**
     * Calculates the remainder of truncating division of this value (dividend) by the other value (divisor).
     */
    operator fun rem(other: MutableUInt32): MutableUInt32 {
        return MutableUInt32(value % other.value)
    }

    /**
     * Shifts this value left by the [bitCount] number of bits.
     *
     * Note that only the five lowest-order bits of the [bitCount] are used as the shift distance.
     * The shift distance actually used is therefore always in the range `0..31`.
     */
    infix fun shl(bitCount: Int): MutableUInt32 {
        return MutableUInt32(value shl bitCount)
    }

    /**
     * Shifts this value right by the [bitCount] number of bits, filling the leftmost bits with zeros.
     *
     * Note that only the five lowest-order bits of the [bitCount] are used as the shift distance.
     * The shift distance actually used is therefore always in the range `0..31`.
     */
    infix fun shr(bitCount: Int): MutableUInt32 {
        return MutableUInt32(value shr bitCount)
    }

    /** Performs a bitwise OR operation between the two values. */
    infix fun or(other: MutableUInt32): MutableUInt32 {
        return MutableUInt32(value or other.value)
    }

    /** Performs a bitwise AND operation between the two values. */
    infix fun and(other: MutableUInt32): MutableUInt32 {
        return MutableUInt32(value and other.value)
    }

    /**
     * Compares this value with the specified value for order.
     *
     * @return Returns zero if this value is equal to the specified other value,
     *   a negative number if it's less than other,
     *   or a positive number if it's greater than other.
     */
    override fun compareTo(other: MutableUInt32): Int {
        return value.compareTo(other.value)
    }

    /**
     * Check if this value is equal to another.
     *
     * An object is equal to this instance only if it is non-null, an
     * instance of [MutableUInt32] and has the same underlying values.
     */
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is MutableUInt32)
            return false
        else
            return value.equals(other.value)
    }

    /**
     * Return a string representation of this class
     */
    override fun toString(): String {
        return value.toString()
    }

    companion object {
        /** The number of bits used to represent a value of this class */
        val SIZE_BITS = MutableUInt32(UInt.SIZE_BITS)

        /** Minimum value representable as [MutableUInt32] */
        val MIN_VALUE = MutableUInt32(UInt.MIN_VALUE)

        /** Maximum value representable as [MutableUInt32] */
        val MAX_VALUE = MutableUInt32(UInt.MAX_VALUE)

        /** Index of the most significant bit in a [MutableUInt32] value */
        const val MSB_INDEX = 31

        /** Index of the least significant bit in a [MutableUInt32] value */
        const val LSB_INDEX = 0
    }
}

fun Number.toMutableUInt32(): MutableUInt32 {
    return MutableUInt32(this)
}

fun UInt.toMutableUInt32(): MutableUInt32 {
    return MutableUInt32(this)
}