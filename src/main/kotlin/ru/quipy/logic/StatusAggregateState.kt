package ru.quipy.logic

import ru.quipy.api.StatusAggregate
import ru.quipy.api.StatusAssignedToTaskEvent
import ru.quipy.api.StatusCreatedEvent
import ru.quipy.api.StatusDeletedEvent
import ru.quipy.api.StatusUpdatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.UUID

class StatusAggregateState : AggregateState<UUID, StatusAggregate> {
    lateinit var statusId: UUID
    lateinit var projectId: UUID
    lateinit var name: String
    lateinit var color: String
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    val assignedTasks = mutableListOf<UUID>()
    var isDelete: Boolean = false
    override fun getId() = statusId

    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        if (isDelete) {
            throw IllegalStateException("Status is deleted")
        }
        statusId = event.statusId
        projectId = event.projectId
        name = event.statusName
        color = event.color
        createdAt = event.createdAt
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusUpdatedApply(event: StatusUpdatedEvent) {
        if (isDelete) {
            throw IllegalStateException("Status is deleted")
        }
        if (statusId != event.statusId) {
            throw IllegalArgumentException("Status ID mismatch: ${event.statusId}")
        }
        name = event.newStatusName
        color = event.newColor
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        if (isDelete) {
            throw IllegalStateException("Status is deleted already")
        }
        if (statusId != event.statusId) {
            throw IllegalArgumentException("Status ID mismatch: ${event.statusId}")
        }
        if (!assignedTasks.isEmpty()) {
            throw IllegalStateException("Status have assigned tasks")
        }
        isDelete = true
        updatedAt = event.createdAt
    }


    @StateTransitionFunc
    fun statusAssignedToTaskApply(event: StatusAssignedToTaskEvent) {
        if (isDelete) {
            throw IllegalStateException("Status is deleted")
        }
        assignedTasks.add(event.taskId)
        updatedAt = event.createdAt
    }
}
