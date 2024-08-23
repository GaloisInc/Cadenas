package com.galois.cadenas.utils

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

const val BIGDECIMAL_PRECISION_DIVIDE: Int = 20

fun BigDecimal.floor(): BigInteger {
    return setScale(0, RoundingMode.FLOOR).toBigInteger()
}

fun BigDecimal.ceil(): BigInteger {
    return setScale(0, RoundingMode.CEILING).toBigInteger()
}


operator fun BigDecimal.plus(v: Int): BigDecimal {
    return this + v.toBigDecimal()
}

operator fun BigDecimal.minus(v: Int): BigDecimal {
    return this - v.toBigDecimal()
}

operator fun BigDecimal.times(v: Int): BigDecimal {
    return this * v.toBigDecimal()
}

operator fun BigDecimal.div(v: Int): BigDecimal {
    return divide(v.toBigDecimal(), BIGDECIMAL_PRECISION_DIVIDE, RoundingMode.HALF_DOWN)
}


operator fun BigDecimal.plus(v: Double): BigDecimal {
    return this + v.toBigDecimal()
}

operator fun BigDecimal.minus(v: Double): BigDecimal {
    return this - v.toBigDecimal()
}

operator fun BigDecimal.times(v: Double): BigDecimal {
    return this * v.toBigDecimal()
}

operator fun BigDecimal.div(v: Double): BigDecimal {
    return divide(v.toBigDecimal(), BIGDECIMAL_PRECISION_DIVIDE, RoundingMode.HALF_DOWN)
}


operator fun BigDecimal.plus(v: Float): BigDecimal {
    return this + v.toBigDecimal()
}

operator fun BigDecimal.minus(v: Float): BigDecimal {
    return this - v.toBigDecimal()
}

operator fun BigDecimal.times(v: Float): BigDecimal {
    return this * v.toBigDecimal()
}

operator fun BigDecimal.div(v: Float): BigDecimal {
    return divide(v.toBigDecimal(), BIGDECIMAL_PRECISION_DIVIDE, RoundingMode.HALF_DOWN)
}

operator fun BigDecimal.plus(v: BigInteger): BigDecimal {
    return this + v.toBigDecimal()
}

operator fun BigDecimal.minus(v: BigInteger): BigDecimal {
    return this - v.toBigDecimal()
}

operator fun BigDecimal.times(v: BigInteger): BigDecimal {
    return this * v.toBigDecimal()
}

operator fun BigDecimal.div(v: BigInteger): BigDecimal {
    return divide(v.toBigDecimal(), BIGDECIMAL_PRECISION_DIVIDE, RoundingMode.HALF_DOWN)
}


operator fun BigInteger.plus(v: Int): BigInteger {
    return this + v.toBigInteger()
}

operator fun BigInteger.minus(v: Int): BigInteger {
    return this - v.toBigInteger()
}

operator fun BigInteger.times(v: Int): BigInteger {
    return this * v.toBigInteger()
}

operator fun BigInteger.div(v: Int): BigDecimal {
    return this.toBigDecimal().divide(v.toBigDecimal(), BIGDECIMAL_PRECISION_DIVIDE, RoundingMode.HALF_DOWN)
}


operator fun BigInteger.plus(v: BigDecimal): BigDecimal {
    return this.toBigDecimal() + v
}

operator fun BigInteger.minus(v: BigDecimal): BigDecimal {
    return this.toBigDecimal() - v
}

operator fun BigInteger.times(v: BigDecimal): BigDecimal {
    return this.toBigDecimal() * v
}

operator fun BigInteger.div(v: BigDecimal): BigDecimal {
    return this.toBigDecimal().divide(v, BIGDECIMAL_PRECISION_DIVIDE, RoundingMode.HALF_DOWN)
}


operator fun BigInteger.plus(v: Double): BigDecimal {
    return this.toBigDecimal() + v
}

operator fun BigInteger.minus(v: Double): BigDecimal {
    return this.toBigDecimal() - v
}

operator fun BigInteger.times(v: Double): BigDecimal {
    return this.toBigDecimal() * v
}

operator fun BigInteger.div(v: Double): BigDecimal {
    return this.toBigDecimal().divide(v.toBigDecimal(), BIGDECIMAL_PRECISION_DIVIDE, RoundingMode.HALF_DOWN)
}


operator fun BigInteger.plus(v: Float): BigDecimal {
    return this.toBigDecimal() + v
}

operator fun BigInteger.minus(v: Float): BigDecimal {
    return this.toBigDecimal() - v
}

operator fun BigInteger.times(v: Float): BigDecimal {
    return this.toBigDecimal() * v
}

operator fun BigInteger.div(v: Float): BigDecimal {
    return this.toBigDecimal().divide(v.toBigDecimal(), BIGDECIMAL_PRECISION_DIVIDE, RoundingMode.HALF_DOWN)
}
