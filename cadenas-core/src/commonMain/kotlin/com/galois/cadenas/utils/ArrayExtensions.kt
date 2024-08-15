package com.galois.cadenas.utils

/**
 * Extension function that performs an inplace update of an array
 * using a mapping function
 * 
 * @param fn Mapping function
 */
fun <T> Array<T>.update(fn: (T) -> T) =
    this.forEachIndexed { i, v -> this[i] = fn(v) }

/**
 * Extension function that performs an inplace update of an IntArray
 * using a mapping function
 *
 * @param fn Mapping function
 */
fun IntArray.update(fn: (Int) -> Int) =
    this.forEachIndexed { i, v -> this[i] = fn(v) }

/**
 * Extension function that performs an inplace update of an FloatArray
 * using a mapping function
 *
 * @param fn Mapping function
 */
fun FloatArray.update(fn: (Float) -> Float) =
    this.forEachIndexed { i, v -> this[i] = fn(v) }

/**
 * Extension function that performs an inplace update of an DoubleArray
 * using a mapping function
 *
 * @param fn Mapping function
 */
fun DoubleArray.update(fn: (Double) -> Double) =
    this.forEachIndexed { i, v -> this[i] = fn(v) }
