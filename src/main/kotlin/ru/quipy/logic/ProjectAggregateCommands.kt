package ru.quipy.logic

import ru.quipy.api.ParticipantAddedEvent
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.StatusAssignedToTaskEvent
import ru.quipy.api.StatusCreatedEvent
import ru.quipy.api.StatusDeletedEvent
import ru.quipy.api.StatusUpdatedEvent
import ru.quipy.api.TagAssignedToTaskEvent
import ru.quipy.api.TagCreatedEvent
import ru.quipy.api.TaskCreatedEvent
import java.util.UUID

fun ProjectAggregateState.create(title: String, creatorId: UUID): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        title = title,
        creatorId = creatorId,
    )
}

fun ProjectAggregateState.createStatus(name: String, color: String): StatusCreatedEvent {
    if (projectStatuses.values.any { it.name == name }) {
        throw IllegalArgumentException("Status already exists: $name")
    }
    return StatusCreatedEvent(
        projectId = this.getId(),
        statusId = UUID.randomUUID(),
        statusName = name,
        color = color
    )
}

fun ProjectAggregateState.updateStatus(statusId: UUID, name: String, color: String): StatusUpdatedEvent {
    if (!projectStatuses.containsKey(statusId)) {
        throw IllegalArgumentException("Status doesn't exists: $statusId")
    }
    if (projectStatuses[statusId]!!.isDelete) {
        throw IllegalStateException("Status is deleted")
    }
    return StatusUpdatedEvent(
        projectId = this.getId(),
        statusId = statusId,
        newStatusName = name,
        newColor = color
    )
}

fun ProjectAggregateState.deleteStatus(statusId: UUID): StatusDeletedEvent {
    if (!projectStatuses.containsKey(statusId)) {
        throw IllegalArgumentException("Status doesn't exists: $statusId")
    }
    if (projectStatuses[statusId]!!.isDelete) {
        throw IllegalStateException("Status is deleted")
    }
    if (tasks.any { it.value.statusId == statusId }) {
        throw IllegalStateException("there are tasks with this status in the project")
    }
    return StatusDeletedEvent(
        projectId = this.getId(),
        statusId = statusId
    )
}

fun ProjectAggregateState.assignStatusToTask(taskId: UUID, statusId: UUID, ): StatusAssignedToTaskEvent {
    if (!projectStatuses.containsKey(statusId)) {
        throw IllegalArgumentException("Status doesn't exists: $statusId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return StatusAssignedToTaskEvent(projectId = this.getId(), taskId = taskId, statusId = statusId)
}

fun ProjectAggregateState.addTask(taskName: String, creatorId: UUID): TaskCreatedEvent {
    return TaskCreatedEvent(
        projectId = this.getId(),
        taskId = UUID.randomUUID(),
        taskName = taskName,
        defaultStatusId = defaultStatus.id,
        creatorId = creatorId
    )
}


fun ProjectAggregateState.addParticipantById(userId: UUID): ParticipantAddedEvent {
    if (participants.contains(userId))
        throw IllegalArgumentException("User $userId is already a participant of the project ${getId()}.")

    return ParticipantAddedEvent(projectId = getId(), userId = userId)
}

fun ProjectAggregateState.createTag(name: String): TagCreatedEvent {
    if (projectTags.values.any { it.name == name }) {
        throw IllegalArgumentException("Tag already exists: $name")
    }
    return TagCreatedEvent(projectId = this.getId(), tagId = UUID.randomUUID(), tagName = name)
}

fun ProjectAggregateState.assignTagToTask(tagId: UUID, taskId: UUID): TagAssignedToTaskEvent {
    if (!projectTags.containsKey(tagId)) {
        throw IllegalArgumentException("Tag doesn't exists: $tagId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return TagAssignedToTaskEvent(projectId = this.getId(), tagId = tagId, taskId = taskId)
}