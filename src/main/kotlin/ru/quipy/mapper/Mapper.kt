package ru.quipy.mapper

import org.springframework.stereotype.Service
import ru.quipy.dto.UserResponse
import ru.quipy.logic.UserAggregateState

@Service
class Mapper {
    fun mapToUserResponse(user: UserAggregateState) : UserResponse =
        UserResponse(
            user.getId(),
            user.userName,
            user.login
        )
}