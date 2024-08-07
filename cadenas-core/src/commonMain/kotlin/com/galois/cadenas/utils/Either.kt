package com.galois.cadenas.mbfte.utils

sealed class Either<T, U> {
    class Left<T, U>(val left: T): Either<T, U>()
    class Right<T, U>(val right: U): Either<T, U>()

    fun isLeft(): Boolean = this is Left

    fun isRight(): Boolean = this is Right

    fun left(): T = when(this) {
        is Left -> left
        else -> throw IllegalStateException("Cannot execute left() when value is Right")
    }

    fun right(): U = when(this) {
        is Right -> right
        else -> throw IllegalStateException("Cannot execute right() when value is Left")
    }
}