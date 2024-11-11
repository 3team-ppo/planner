package ru.quipy.logic

import ru.quipy.api.*
import java.util.*

fun StatusAggregateState.createStatus(
    statusName: String,
    color: String,
    projectId: UUID
): StatusCreatedEvent {
    return StatusCreatedEvent(
        projectId = projectId,
        statusId = UUID.randomUUID(),
        statusName = statusName,
        color = color
    )
}

fun StatusAggregateState.updateStatus(newStatusName: String, newColor: String, projectId: UUID): StatusUpdatedEvent {
    if (this.getId() == UUID(0, 0)) {
        throw IllegalStateException("Status ID is not initialized")
    }
    return StatusUpdatedEvent(
        projectId = projectId,
        statusId = this.getId(),
        newStatusName = newStatusName,
        newColor = newColor
    )
}

fun StatusAggregateState.assignStatusToTask(taskId: UUID, projectId: UUID): StatusAssignedToTaskEvent {
    if (this.getId() == UUID(0, 0)) {
        throw IllegalStateException("Status ID is not initialized")
    }
    return StatusAssignedToTaskEvent(
        projectId = projectId,
        taskId = taskId,
        statusId = this.getId()
    )
}
