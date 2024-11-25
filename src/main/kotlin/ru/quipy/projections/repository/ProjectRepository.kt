package ru.quipy.projections.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import ru.quipy.projections.entity.Project
import java.util.UUID

@Repository
interface ProjectRepository : MongoRepository<Project, UUID> {
}
