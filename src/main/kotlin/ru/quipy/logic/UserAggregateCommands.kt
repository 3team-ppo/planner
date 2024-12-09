package ru.quipy.logic

import ru.quipy.api.*

fun UserAggregateState.createUser(
    userName: String,
    login: String,
    password: String
): UserCreatedEvent {
    return UserCreatedEvent(
        userName = userName,
        login = login,
        password = password,
        createdAt = System.currentTimeMillis()
    )
}

fun UserAggregateState.updateUser(
    newName: String,
    newLogin: String,
    newPassword: String
): UserUpdatedEvent {
    return UserUpdatedEvent(
        userId = this.getId(),
        newName = newName,
        newLogin = newLogin,
        newPassword = newPassword,
        createdAt = System.currentTimeMillis()
    )
}
