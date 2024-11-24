package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.TaskAggregateState
import ru.quipy.logic.updateTask
import ru.quipy.logic.changeTaskStatus
import ru.quipy.logic.assignTaskToUser
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>
) {

    @PostMapping("/{taskId}/update")
    fun updateTask(
        @PathVariable taskId: UUID,
        @RequestParam projectId: UUID,
        @RequestParam newTaskName: String,
        @RequestParam newPriority: Int,
        @RequestParam newEstimatedTime: Int,
        @RequestParam newAssigneeIds: List<UUID>
    ): TaskUpdatedEvent {
        return taskEsService.update(taskId) {
            it.updateTask(
                projectId,
                newTaskName,
                newPriority,
                newEstimatedTime,
                newAssigneeIds
            )
        }
    }

    @PostMapping("/{taskId}/status/{newStatusId}")
    fun changeTaskStatus(
        @PathVariable taskId: UUID,
        @PathVariable newStatusId: UUID,
        @RequestParam projectId: UUID
    ): TaskStatusChangedEvent {
        return taskEsService.update(taskId) {
            it.changeTaskStatus(newStatusId, projectId)
        }
    }

    @PostMapping("/{taskId}/assign")
    fun assignTaskToUser(
        @PathVariable taskId: UUID,
        @RequestParam assigneeId: UUID,
        @RequestParam projectId: UUID
    ): TaskAssignedToUserEvent {
        return taskEsService.update(taskId) {
            it.assignTaskToUser(assigneeId, projectId)
        }
    }

    @GetMapping("/{taskId}")
    fun getTask(@PathVariable taskId: UUID): TaskAggregateState? {
        return taskEsService.getState(taskId)
    }
}
