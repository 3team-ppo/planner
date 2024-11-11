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
    var createdDate: Long = System.currentTimeMillis()
    var lastModifiedDate: Long = System.currentTimeMillis()

    override fun getId() = taskId

    @StateTransitionFunc
    fun taskUpdatedApply(event: TaskUpdatedEvent) {
        if (taskId != event.taskId) {
            throw IllegalArgumentException("Task ID mismatch: ${event.taskId}")
        }
        taskName = event.newTaskName
        statusId = event.newStatusId
        priority = event.newPriority
        estimatedTime = event.newEstimatedTime
        assigneeIds = event.newAssigneeIds.toMutableList()
        lastModifiedDate = event.updatedAt
    }

    @StateTransitionFunc
    fun taskStatusChangedApply(event: TaskStatusChangedEvent) {
        if (taskId != event.taskId) {
            throw IllegalArgumentException("Task ID mismatch: ${event.taskId}")
        }
        statusId = event.newStatusId
        lastModifiedDate = event.createdAt
    }

    @StateTransitionFunc
    fun taskAssignedToUserApply(event: TaskAssignedToUserEvent) {
        if (taskId != event.taskId) {
            throw IllegalArgumentException("Task ID mismatch: ${event.taskId}")
        }
        assigneeIds.add(event.assigneeId)
        lastModifiedDate = event.createdAt
    }

    @StateTransitionFunc
    fun taskCompletedApply(event: TaskCompletedEvent) {
        if (taskId != event.taskId) {
            throw IllegalArgumentException("Task ID mismatch: ${event.taskId}")
        }
        lastModifiedDate = event.createdAt
    }
}
