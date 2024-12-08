package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import ru.quipy.api.UserCreatedEvent
import ru.quipy.controller.ProjectController
import ru.quipy.controller.TaskController
import ru.quipy.controller.UserController
import ru.quipy.logic.Task

@SpringBootTest
@ContextConfiguration(classes = [DemoApplication::class])
class TaskControllerTests {
    @Autowired
    private lateinit var userController: UserController

    @Autowired
    private lateinit var projectController: ProjectController

    @Autowired
    private lateinit var taskController: TaskController

    @Test
    fun All_createTaskAndStatuses_TaskAndStatusesCreatedWithCorrectFields() {
        val owner = createUser("ulyana")
        val user = createUser("anya")
        val project = createProject(owner)
        val task = projectController.createTask(project.projectId, "do_lab_6", user.userId)
        val expected = Task(task.taskId, task.taskName, project.defaultStatusId, mutableSetOf())

        var actualTasks = projectController.getProject(project.projectId)?.tasks
        var actualStatuses = projectController.getProject(project.projectId)?.projectStatuses

        Assertions.assertNotNull(actualTasks)
        Assertions.assertEquals(1, actualTasks!!.size)
        Assertions.assertEquals(expected, actualTasks[task.taskId])

        Assertions.assertEquals(3, actualStatuses!!.size)
        Assertions.assertTrue(actualStatuses.any { it.value.name == "CREATED" })
        Assertions.assertTrue(actualStatuses.any { it.value.name == "UNCOMPLETED" })
        Assertions.assertTrue(actualStatuses.any { it.value.name == "COMPLETED" })

        var actualTask = taskController.getTask(task.taskId)
        Assertions.assertEquals(project.projectId, actualTask!!.projectId)
    }

    @Test
    fun ChangeTaskStatus_StatusChanged() {
        val owner = createUser("ulyana")
        val user = createUser("anya")
        val project = createProject(owner)
        val task = projectController.createTask(project.projectId, "do_lab_6", user.userId)

        var actualStatuses = projectController.getProject(project.projectId)?.projectStatuses
        val completedId = actualStatuses!!.entries.find { it.value.name == "COMPLETED" }!!.value.id

        taskController.changeTaskStatus(
            task.taskId,
            completedId,
            project.projectId
        )

        val actualTaskUpdatedStatus = taskController.getTask(task.taskId)
        Assertions.assertNotNull(actualTaskUpdatedStatus)
        Assertions.assertEquals(completedId, actualTaskUpdatedStatus!!.statusId)
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