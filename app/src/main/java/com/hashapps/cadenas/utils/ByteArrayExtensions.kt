package com.hashapps.cadenas.utils

fun ByteArray.toHex(): String = joinToString(separator = "") { "%02x".format(it) }

fun String.toHexBytes(): ByteArray = chunked(2).map { b -> b.toInt(16).toByte() }.toByteArray()