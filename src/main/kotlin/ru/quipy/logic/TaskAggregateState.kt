package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class TaskAggregateState : AggregateState<UUID, TaskAggregate> {
    private lateinit var taskId: UUID
    lateinit var taskName: String
    lateinit var projectId: UUID
    lateinit var statusId: UUID

    var priority: Int = 0
    var estimatedTime: Int = 0
    var assigneeIds: MutableList<UUID> = mutableListOf()
    lateinit var creatorId: UUID

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    override fun getId() = taskId

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        taskId = event.taskId
        taskName = event.taskName
        projectId = event.projectId
        statusId = event.defaultStatusId
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskUpdatedApply(event: TaskUpdatedEvent) {
        taskName = event.newTaskName
        priority = event.newPriority
        estimatedTime = event.newEstimatedTime
        assigneeIds = event.newAssigneeIds.toMutableList()
        updatedAt = event.updatedAt
    }

    @StateTransitionFunc
    fun taskStatusChangedApply(event: TaskStatusChangedEvent) {
        statusId = event.newStatusId
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskAssignedToUserApply(event: TaskAssignedToUserEvent) {
        assigneeIds.add(event.assigneeId)
        updatedAt = event.createdAt
    }
}
