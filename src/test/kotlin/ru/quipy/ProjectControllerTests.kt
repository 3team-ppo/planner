package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.UserCreatedEvent
import ru.quipy.controller.ProjectController
import ru.quipy.controller.UserController

@SpringBootTest
@TestPropertySource(locations = ["classpath:application.properties"])
@ContextConfiguration(classes = [DemoApplication::class])
class ProjectControllerTests {

    @Autowired
    private lateinit var userController: UserController

    @Autowired
    private lateinit var projectController: ProjectController

    @Test
    fun createProject_ProjectCreatedWithCorrectFieldsAndCreatedAsParticipant() {
        val owner: UserCreatedEvent = createUser("ulyana")
        val project: ProjectCreatedEvent = createProject(owner)
        Assertions.assertEquals(1, project.version)
        Assertions.assertEquals("theBestProject", project.title)

        val gotProject = projectController.getProject(project.projectId)
        Assertions.assertNotNull(gotProject)

        val ownerInProject = gotProject!!.participants.firstOrNull() { it == owner.userId }
        Assertions.assertNotNull(ownerInProject)
        Assertions.assertEquals("theBestProject", gotProject.projectTitle)
        Assertions.assertEquals(owner.userId, ownerInProject!!)
        Assertions.assertEquals(1, gotProject.participants.size)
    }

    @Test
    fun createAddParticipant_ParticipantAdded() {
        val owner = createUser("ulyana")
        val user = createUser("anya")
        val project = createProject(owner)

        var gotProject = projectController.getProject(project.projectId)
        Assertions.assertNotNull(gotProject)

        projectController.addParticipant(
            gotProject!!.getId(),
            user.userId
        )

        gotProject = projectController.getProject(gotProject.getId())

        val userInProject = gotProject!!.participants.firstOrNull() { it == user.userId }

        Assertions.assertNotNull(userInProject)
        Assertions.assertEquals(user.userId, userInProject!!)
        Assertions.assertEquals(2, gotProject.participants.size)
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