package com.hashapps.cadenas.ui.model

import com.hashapps.cadenas.data.model.Model

data class ModelUiState(
    val id: Int = 0,
    val name: String = "",
    val url: String = "",
    val actionEnabled: Boolean = false,
)

fun ModelUiState.toModel(): Model = Model(
    id = id,
    name = name,
    url = url,
)

val nameRegex =
    Regex("""[a-zA-Z\d]+""")
fun ModelUiState.isNameValid() = nameRegex.matches(name)

private val urlRegex =
    Regex("""https://(www\.)?[-a-zA-Z\d@:%._+~#=]{1,256}\.[a-zA-Z\d()]{1,6}\b([-a-zA-Z\d()!@:%_+.~#?&/=]*)""")
fun ModelUiState.isUrlValid() = urlRegex.matches(url)

fun ModelUiState.isValid() =
    isNameValid() && isUrlValid()