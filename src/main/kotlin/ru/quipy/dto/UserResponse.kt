package ru.quipy.dto

import java.util.UUID

class UserResponse(
    val userId: UUID,
    val userName: String,
    val login: String
)