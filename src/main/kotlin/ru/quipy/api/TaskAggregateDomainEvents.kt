package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val TASK_UPDATED_EVENT = "TASK_UPDATED_EVENT"
const val TASK_STATUS_CHANGED_EVENT = "TASK_STATUS_CHANGED_EVENT"
const val TASK_ASSIGNED_TO_USER_EVENT = "TASK_ASSIGNED_TO_USER_EVENT"
const val TASK_COMPLETED_EVENT = "TASK_COMPLETED_EVENT"

// API
@DomainEvent(name = TASK_UPDATED_EVENT)
class TaskUpdatedEvent(
    val taskId: UUID,
    val projectId: UUID,
    val newTaskName: String,
    val newStatusId: UUID,
    val newPriority: Int,
    val newEstimatedTime: Int,
    val newAssigneeIds: List<UUID>,
    val updatedAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = TASK_UPDATED_EVENT,
    createdAt = updatedAt,
)

@DomainEvent(name = TASK_STATUS_CHANGED_EVENT)
class TaskStatusChangedEvent(
    val taskId: UUID,
    val projectId: UUID,
    val newStatusId: UUID,
    changedAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = TASK_STATUS_CHANGED_EVENT,
    createdAt = changedAt,
)

@DomainEvent(name = TASK_ASSIGNED_TO_USER_EVENT)
class TaskAssignedToUserEvent(
    val taskId: UUID,
    val projectId: UUID,
    val assigneeId: UUID,
    assignedAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = TASK_ASSIGNED_TO_USER_EVENT,
    createdAt = assignedAt,
)

@DomainEvent(name = TASK_COMPLETED_EVENT)
class TaskCompletedEvent(
    val taskId: UUID,
    val projectId: UUID,
    completedAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = TASK_COMPLETED_EVENT,
    createdAt = completedAt,
)
