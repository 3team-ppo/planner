package ru.quipy.projections.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import ru.quipy.projections.entity.User
import java.util.UUID

@Repository
interface UserRepository  : MongoRepository<User, UUID> {
}