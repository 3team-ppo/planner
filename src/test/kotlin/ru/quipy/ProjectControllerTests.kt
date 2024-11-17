package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import ru.quipy.api.UserCreatedEvent
import ru.quipy.controller.ProjectController
import ru.quipy.controller.UserController

@SpringBootTest
@ContextConfiguration(classes = [DemoApplication::class])
class ProjectControllerTests {

    @Autowired
    private lateinit var userController: UserController

    @Autowired
    private lateinit var projectController: ProjectController

    @Test
    fun createProject_ProjectCreatedWithCorrectFieldsAndCreatedAsParticipant() {
        val owner = createUser("ulyana")
        val project = projectController.createProject(
            "theBestProject",
            owner.userId
        )
        Assertions.assertEquals(1, project.version)
        Assertions.assertEquals("project", project.title)

        var gotProject = projectController.getProject(project.projectId)
        Assertions.assertNotNull(gotProject)

        val ownerInProject = gotProject!!.participants.firstOrNull() { it == owner.userId }
        Assertions.assertNotNull(ownerInProject)
        Assertions.assertEquals("theBestProject", gotProject.projectTitle)
        Assertions.assertEquals(owner.userId, ownerInProject!!)
        Assertions.assertEquals(1, gotProject.participants.size)
    }

    private fun createUser(name: String): UserCreatedEvent {
        return userController.createUser(
            "$name",
            name,
            "12345678"
        )
    }
}