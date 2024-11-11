package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class StatusAggregateState : AggregateState<UUID, StatusAggregate> {
    private lateinit var statusId: UUID
    private lateinit var projectId: UUID
    lateinit var name: String
    lateinit var color: String
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    private val assignedTasks = mutableListOf<UUID>()  // Track assigned tasks

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
    fun statusAssignedToTaskApply(event: StatusAssignedToTaskEvent) {
        assignedTasks.add(event.taskId)
        updatedAt = event.createdAt
    }
}
