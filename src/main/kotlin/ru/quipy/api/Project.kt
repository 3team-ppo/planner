package ru.quipy.api

import java.time.LocalDateTime
import java.util.UUID

data class Project(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val creatorId: UUID,
    val createdDate: LocalDateTime = LocalDateTime.now(),
    val lastModifiedDate: LocalDateTime = LocalDateTime.now(),
    val participantIds: List<UUID> = listOf(),
    val taskIds: List<UUID> = listOf()
)
