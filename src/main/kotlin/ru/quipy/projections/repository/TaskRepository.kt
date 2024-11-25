package ru.quipy.projections.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import ru.quipy.projections.entity.MongoTask
import java.util.UUID

@Repository
interface TaskRepository : MongoRepository<MongoTask, UUID> {
}