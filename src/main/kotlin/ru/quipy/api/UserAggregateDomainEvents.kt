package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val USER_CREATED_EVENT = "USER_CREATED_EVENT"
const val USER_UPDATED_EVENT = "USER_UPDATED_EVENT"

// API
@DomainEvent(name = USER_CREATED_EVENT)
class UserCreatedEvent(
    val userId: UUID = UUID.randomUUID(),
    val userName: String,
    val login: String,
    val password: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<UserAggregate>(
    name = USER_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = USER_UPDATED_EVENT)
class UserUpdatedEvent(
    val userId: UUID,
    val newName: String,
    val newLogin: String,
    val newPassword: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<UserAggregate>(
    name = USER_UPDATED_EVENT,
    createdAt = createdAt,
)