package ru.quipy.api

import java.util.UUID

data class Status(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val color: String,
    val projectId: UUID
)
