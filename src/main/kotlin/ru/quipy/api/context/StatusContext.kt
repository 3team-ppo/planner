package ru.quipy.api.context

import ru.quipy.api.Status
import java.util.UUID

class StatusContext {
    private val statuses = mutableMapOf<UUID, Status>()

    fun addStatus(status: Status) {
        statuses[status.id] = status
    }

    fun getStatus(id: UUID): Status? = statuses[id]

    fun getAllStatuses(): List<Status> = statuses.values.toList()
}
