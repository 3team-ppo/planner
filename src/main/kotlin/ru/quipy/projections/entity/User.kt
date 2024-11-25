package ru.quipy.projections.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document(collection = "users")
data class User(
    @Id
    val userId: UUID,
    val userName: String,
    val login: String,
    val password: String,
    var createdAt: Long = System.currentTimeMillis()
)