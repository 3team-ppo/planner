package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.UserCreatedEvent
import ru.quipy.controller.ProjectController
import ru.quipy.controller.UserController
import ru.quipy.projections.entity.Project
import ru.quipy.projections.repository.ProjectRepository

@SpringBootTest
@TestPropertySource(locations = ["classpath:application.properties"])
@ContextConfiguration(classes = [DemoApplication::class])
class ProjectionProjectControllerTest {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var projectRepository: ProjectRepository

    @Autowired
    private lateinit var userController: UserController

    @Autowired
    private lateinit var projectController: ProjectController

    @BeforeEach
    fun setUp() {
        mongoTemplate.dropCollection(Project::class.java)
    }

    @Test
    fun createProject_ProjectCreatedWithCorrectFieldsAndCreatedAsParticipant() {
        val owner1: UserCreatedEvent = createUser("ulyana")
        val owner2: UserCreatedEvent = createUser("anya")

        val project1: ProjectCreatedEvent = createProject(owner1)
        val project2: ProjectCreatedEvent = createProject(owner2)
        val createdProjects: MutableList<Project> = projectRepository.findAll()

        Assertions.assertNotNull(createdProjects)
        Assertions.assertEquals(2, createdProjects.size)
        Assertions.assertNotNull(createdProjects.find {it.projectId == project1.projectId})
        Assertions.assertNotNull(createdProjects.find {it.projectId == project2.projectId})
    }

    private fun createProject(owner: UserCreatedEvent) = projectController.createProject(
        "theBestProject",
        owner.userId
    )

    private fun createUser(name: String): UserCreatedEvent {
        return userController.createUser(
            name,
            name,
            "12345678"
        )
    }
}