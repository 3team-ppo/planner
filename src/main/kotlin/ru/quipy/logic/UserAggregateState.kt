package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    lateinit var userName: String
    lateinit var login: String
    lateinit var password: String
    var createdAt: Long = System.currentTimeMillis()

    override fun getId() = userId

    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        userId = event.userId
        userName = event.userName
        login = event.login
        password = event.password
        createdAt = event.createdAt
    }

    @StateTransitionFunc
    fun userUpdatedApply(event: UserUpdatedEvent) {
        if (userId != event.userId) {
            throw IllegalArgumentException("User ID mismatch: ${event.userId}")
        }
        userName = event.newName
        login = event.newLogin
        password = event.newPassword
        createdAt = event.createdAt
    }
}
