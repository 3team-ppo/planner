package ru.quipy.api.context

import ru.quipy.api.User
import java.util.UUID

class UserContext {
    private val users = mutableMapOf<UUID, User>()

    fun addUser(user: User) {
        users[user.id] = user
    }

    fun getUser(id: UUID): User? = users[id]

    fun getAllUsers(): List<User> = users.values.toList()
}
