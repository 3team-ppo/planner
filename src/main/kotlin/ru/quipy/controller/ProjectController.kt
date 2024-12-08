package ru.quipy.controller

import javassist.NotFoundException
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.ParticipantAddedEvent
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.StatusCreatedEvent
import ru.quipy.api.StatusDeletedEvent
import ru.quipy.api.StatusUpdatedEvent
import ru.quipy.api.TaskAddedEvent
import ru.quipy.api.TaskAggregate
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.TaskAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.addParticipantById
import ru.quipy.logic.addTask
import ru.quipy.logic.create
import ru.quipy.logic.createStatus
import ru.quipy.logic.deleteStatus
import ru.quipy.logic.updateStatus
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>
) {

    @PostMapping("/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: UUID) : ProjectCreatedEvent {
        return projectEsService.create { it.create(projectTitle, creatorId) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/createStatus/{statusName}")
    fun createStatus(
        @PathVariable statusName: String,
        @PathVariable projectId: UUID,
        @RequestParam color: String
    ): StatusCreatedEvent {
        return projectEsService.update(projectId) { it.createStatus(statusName, color) }
    }

    @PostMapping("/{projectId}/updateStatus/{statusId}")
    fun updateStatus(
        @PathVariable statusId: UUID,
        @PathVariable projectId: UUID,
        @RequestParam newStatusName: String,
        @RequestParam newColor: String,
    ): StatusUpdatedEvent {
        return projectEsService.update(projectId) {
            it.updateStatus(statusId, newStatusName, newColor)
        }
    }

    @DeleteMapping("/{projectId}/deleteStatus/{statusId}")
    fun deleteStatus(
        @PathVariable statusId: UUID,
        @PathVariable projectId: UUID
    ): StatusDeletedEvent {
        return projectEsService.update(projectId) {
            it.deleteStatus(statusId)
        }
    }

    @PostMapping("/{projectId}/tasks/{taskName}")
    fun createTask(
        @PathVariable projectId: UUID,
        @PathVariable taskName: String,
        @RequestParam creatorId: UUID
    ) : TaskCreatedEvent {
       val event = projectEsService.update(projectId) {
            it.addTask(taskName, creatorId)
        }
        return taskEsService.create {
            it.create(projectId, event.taskId, event.taskName, creatorId, event.defaultStatusId)
        }
    }

    @PostMapping("/{projectId}/participants")
    fun addParticipant(
        @PathVariable projectId: UUID,
        @RequestParam userId: UUID
    ): ParticipantAddedEvent {
        val user = userEsService.getState(userId) ?: throw NotFoundException("User $userId wasn't not found.")

        return projectEsService.update(projectId) { it.addParticipantById(userId = userId) }
    }
}