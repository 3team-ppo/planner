package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.dto.UserResponse
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.createUser
import ru.quipy.logic.updateUser
import ru.quipy.mapper.Mapper
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
    val mapper: Mapper
) {

    @PostMapping("/create")
    fun createUser(
        @RequestParam userName: String,
        @RequestParam login: String,
        @RequestParam password: String
    ): UserCreatedEvent {
        return userEsService.create { it.createUser(userName, login, password) }
    }

    @PostMapping("/{userId}/update")
    fun updateUser(
        @PathVariable userId: UUID,
        @RequestParam newName: String,
        @RequestParam newLogin: String,
        @RequestParam newPassword: String
    ): UserUpdatedEvent {
        return userEsService.update(userId) {
            it.updateUser(newName, newLogin, newPassword)
        }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID): UserResponse {
        return mapper.mapToUserResponse(userEsService.getState(userId)!!)
    }
}
