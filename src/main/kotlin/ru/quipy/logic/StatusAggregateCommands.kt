package ru.quipy.logic

import ru.quipy.api.StatusCreatedEvent
import ru.quipy.api.StatusDeletedEvent
import ru.quipy.api.StatusUpdatedEvent
import java.util.UUID

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


fun StatusAggregateState.deleteStatus(): StatusDeletedEvent {
    if (this.getId() == UUID(0, 0)) {
        throw IllegalStateException("Status ID is not initialized")
    }
    return StatusDeletedEvent(
        projectId = this.projectId,
        statusId = this.getId()
    )
}
