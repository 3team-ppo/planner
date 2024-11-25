package ru.quipy.projections.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document(collection = "tasks")
data class MongoTask(
    @Id
    val taskId: UUID,

    val taskName: String,

    val projectId: UUID,

    val statusId: UUID,

    val priority: Int = 0,

    val estimatedTime: Int = 0,

    val assigneeIds: List<UUID> = emptyList(),

    val creatorId: UUID,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()
)