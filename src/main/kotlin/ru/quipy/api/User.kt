package ru.quipy.api

import java.util.UUID

data class User(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val login: String,
    val password: String
)
