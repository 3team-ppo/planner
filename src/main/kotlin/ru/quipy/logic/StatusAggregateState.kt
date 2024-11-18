package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class StatusAggregateState : AggregateState<UUID, StatusAggregate> {
    lateinit var statusId: UUID
    lateinit var projectId: UUID
    lateinit var name: String
    lateinit var color: String
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    val assignedTasks = mutableListOf<UUID>()

    override fun getId() = statusId

    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        statusId = event.statusId
        projectId = event.projectId
        name = event.statusName
        color = event.color
        createdAt = event.createdAt
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusUpdatedApply(event: StatusUpdatedEvent) {
        if (statusId != event.statusId) {
            throw IllegalArgumentException("Status ID mismatch: ${event.statusId}")
        }
        name = event.newStatusName
        color = event.newColor
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        if (statusId != event.statusId) {
            throw IllegalArgumentException("Status ID mismatch: ${event.statusId}")
        }
        if (!assignedTasks.isEmpty()) {
            throw IllegalStateException("Status have assigned tasks")
        }



    }


    @StateTransitionFunc
    fun statusAssignedToTaskApply(event: StatusAssignedToTaskEvent) {
        assignedTasks.add(event.taskId)
        updatedAt = event.createdAt
    }
}
