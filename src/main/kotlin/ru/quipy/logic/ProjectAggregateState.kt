package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    lateinit var projectTitle: String

    lateinit var creatorId: UUID
    var participants = mutableListOf<UUID>()

    var tasks = mutableMapOf<UUID, Task>()

    var projectStatuses = mutableMapOf<UUID, Status>()
    var projectTags = mutableMapOf<UUID, TagEntity>()

    lateinit var defaultStatus : Status

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()


    override fun getId() = projectId

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = UUID.randomUUID()
        projectTitle = event.title

        creatorId = event.creatorId
        participants.add(element = event.creatorId)

        defaultStatus = Status(name = "CREATED", color = "BLUE")
        projectStatuses[defaultStatus.id] = defaultStatus

        val uncompletedStatus = Status(name = "UNCOMPLETED", color = "RED")
        val completedStatus = Status(name = "COMPLETED", color = "GREEN")
        projectStatuses[uncompletedStatus.id] = uncompletedStatus
        projectStatuses[completedStatus.id] = completedStatus

        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        projectStatuses[event.statusId] = Status(name = event.statusName, color = event.color)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusUpdatedApply(event: StatusUpdatedEvent) {
        projectStatuses[event.statusId] = Status(name = event.newStatusName, color = event.newColor)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        projectStatuses[event.statusId]!!.isDelete = true
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusAssignedToTaskEventApply(event: StatusAssignedToTaskEvent) {
        if (projectStatuses[event.statusId]!!.isDelete) {
            throw IllegalStateException("Status is deleted")
        }
        tasks[event.taskId]!!.statusId = event.statusId
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = Task(event.taskId, event.taskName, defaultStatus.id)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun participantAddedApply(event: ParticipantAddedEvent) {
        participants.add(element = event.userId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusChangedApply(event: TaskStatusChangedEvent) {
        tasks[event.taskId]!!.statusId = event.newStatusId
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun tagCreatedApply(event: TagCreatedEvent) {
        projectTags[event.tagId] = TagEntity(event.tagId, event.tagName)
        updatedAt = createdAt
    }
}

data class Task(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    var statusId: UUID,
    val tagsAssigned: MutableSet<UUID> = mutableSetOf()
)

data class Status(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val color: String,
    var isDelete: Boolean = false
)

data class TagEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String
)

@StateTransitionFunc
fun ProjectAggregateState.tagAssignedApply(event: TagAssignedToTaskEvent) {
    tasks[event.taskId]?.tagsAssigned?.add(event.tagId)
        ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    updatedAt = createdAt
}
