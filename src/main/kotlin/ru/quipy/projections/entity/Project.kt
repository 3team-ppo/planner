package ru.quipy.projections.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document(collection = "projects")
data class Project(
    @Id
    val projectId: UUID,

    val projectTitle: String,

    val creatorId: UUID,

    val defaultStatus : Status,

    val participants: List<UUID> = emptyList(),

    val tasks: List<Task> = emptyList(),

    val projectStatuses: List<Status> = emptyList(),

    val projectTags: List<UUID> = emptyList(),

    var createdAt: Long = System.currentTimeMillis(),

    var updatedAt: Long = System.currentTimeMillis(),
)

data class Status(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val color: String,
    var isDelete: Boolean = false
)

data class Task(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    var statusId: UUID,
)