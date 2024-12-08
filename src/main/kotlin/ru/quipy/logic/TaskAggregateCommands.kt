package ru.quipy.logic

import ru.quipy.api.*
import java.util.*

fun TaskAggregateState.updateTask(
    projectId: UUID,
    newTaskName: String,
    newPriority: Int,
    newEstimatedTime: Int,
    newAssigneeIds: List<UUID>
): TaskUpdatedEvent {
    return TaskUpdatedEvent(
        taskId = this.getId(),
        projectId = projectId,
        newTaskName = newTaskName,
        newPriority = newPriority,
        newEstimatedTime = newEstimatedTime,
        newAssigneeIds = newAssigneeIds,
        updatedAt = System.currentTimeMillis()
    )
}

fun TaskAggregateState.changeTaskStatus(newStatusId: UUID, projectId: UUID): TaskStatusChangedEvent {
    return TaskStatusChangedEvent(
        taskId = this.getId(),
        projectId = projectId,
        newStatusId = newStatusId,
        changedAt = System.currentTimeMillis()
    )
}

fun TaskAggregateState.assignTaskToUser(assigneeId: UUID, projectId: UUID): TaskAssignedToUserEvent {
    return TaskAssignedToUserEvent(
        taskId = this.getId(),
        projectId = projectId,
        assigneeId = assigneeId,
        assignedAt = System.currentTimeMillis()
    )
}

fun TaskAggregateState.create(projectId: UUID, taskId: UUID, taskName: String, creatorId: UUID, defaultStatus: UUID): TaskCreatedEvent {
    return TaskCreatedEvent(
        projectId = projectId,
        taskId = taskId,
        taskName = taskName,
        defaultStatusId = defaultStatus,
        creatorId = creatorId
    )
}
