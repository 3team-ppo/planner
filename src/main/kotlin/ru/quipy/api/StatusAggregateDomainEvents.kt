package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val STATUS_CREATED_EVENT = "STATUS_CREATED_EVENT"
const val STATUS_UPDATED_EVENT = "STATUS_UPDATED_EVENT"
const val STATUS_DELETED_EVENT = "STATUS_UPDATED_EVENT"
const val STATUS_ASSIGNED_TO_TASK_EVENT = "STATUS_ASSIGNED_TO_TASK_EVENT"

// API
@DomainEvent(name = STATUS_CREATED_EVENT)
class StatusCreatedEvent(
    val projectId: UUID,
    val statusId: UUID,
    val statusName: String,
    val color: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<StatusAggregate>(
    name = STATUS_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = STATUS_UPDATED_EVENT)
class StatusUpdatedEvent(
    val projectId: UUID,
    val statusId: UUID,
    val newStatusName: String,
    val newColor: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<StatusAggregate>(
    name = STATUS_UPDATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = STATUS_DELETED_EVENT)
class StatusDeletedEvent(
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<StatusAggregate>(
    name = STATUS_DELETED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = STATUS_ASSIGNED_TO_TASK_EVENT)
class StatusAssignedToTaskEvent(
    val projectId: UUID,
    val taskId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<StatusAggregate>(
    name = STATUS_ASSIGNED_TO_TASK_EVENT,
    createdAt = createdAt
)
