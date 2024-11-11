package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.StatusAggregate
import ru.quipy.api.StatusCreatedEvent
import ru.quipy.api.StatusUpdatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.StatusAggregateState
import ru.quipy.logic.createStatus
import ru.quipy.logic.updateStatus
import java.util.*

@RestController
@RequestMapping("/statuses")
class StatusController(
    val statusService: EventSourcingService<UUID, StatusAggregate, StatusAggregateState>
) {

    @PostMapping("/{statusName}")
    fun createStatus(
        @PathVariable statusName: String,
        @RequestParam projectId: UUID,
        @RequestParam color: String
    ): StatusCreatedEvent {
        return statusService.create { it.createStatus(statusName, color, projectId) }
    }

    @GetMapping("/{statusId}")
    fun getStatus(@PathVariable statusId: UUID): StatusAggregateState? {
        return statusService.getState(statusId)
    }

    @PostMapping("/{statusId}/update")
    fun updateStatus(
        @PathVariable statusId: UUID,
        @RequestParam newStatusName: String,
        @RequestParam newColor: String,
        @RequestParam projectId: UUID,
    ): StatusUpdatedEvent {
        return statusService.update(statusId) {
            it.updateStatus(newStatusName, newColor, projectId)
        }
    }
}
