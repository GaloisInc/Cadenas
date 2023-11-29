package com.hashapps.cadenas.utils

fun ByteArray.toHex(): String = joinToString(separator = "") { "%02x".format(it) }