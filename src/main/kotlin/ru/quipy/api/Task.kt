package ru.quipy.api

import java.time.LocalDateTime
import java.util.UUID

data class Task(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val statusId: UUID,
    val priority: Int,
    val creatorId: UUID,
    val createdDate: LocalDateTime = LocalDateTime.now(),
    val lastModifiedDate: LocalDateTime = LocalDateTime.now(),
    val estimatedTime: Int,
    val assigneeIds: List<UUID> = listOf(),
    val projectId: UUID
)
