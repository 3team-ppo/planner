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
        val task = projectController.createTask(project.id, "do_lab_6")

        var actualTasks = projectController.getProject(project.id)?.tasks

        Assertions.assertNotNull(actualTasks)
        Assertions.assertEquals(1, actualTasks!!.size)
        Assertions.assertEquals("CREATED", actualTasks.get(task.taskId))
        Assertions.assertEquals(ColorEnum.GREEN, taskAggregate.getStatuses()[0].color)
        Assertions.assertEquals(1, taskAggregate.getStatuses()[0].position)
        Assertions.assertEquals(0, taskAggregate.getTasks().size)

        val defStatus = taskController.getStatus(projectId, taskAggregate.getStatuses()[0].id)
        defStatusId = defStatus!!.id

        Assertions.assertNotNull(defStatus)
        Assertions.assertEquals("CREATED", defStatus!!.name)
        Assertions.assertEquals(ColorEnum.GREEN, defStatus.color)
        Assertions.assertEquals(1, defStatus.position)

        val status2 = taskController.createStatus(
            projectId,
            "In progress",
            "YELLOW"
        )
        statusId2 = status2.statusId

        val statusAgg2 = taskController.getStatus(projectId, status2.statusId)
        Assertions.assertNotNull(statusAgg2)
        Assertions.assertEquals("In progress", statusAgg2!!.name)
        Assertions.assertEquals(ColorEnum.YELLOW, statusAgg2.color)
        Assertions.assertEquals(2, statusAgg2.position)

        val task = taskController.createTask(
            projectId,
            "Task",
            "Task d",
            status2.statusId,
        )
        taskId = task.taskId

        val taskAgg = taskController.getTask(projectId, task.taskId)
        Assertions.assertNotNull(taskAgg)
        Assertions.assertEquals("Task", taskAgg!!.name)
        Assertions.assertEquals("Task d", taskAgg.description)
        Assertions.assertEquals(0, taskAgg.executors.size)
        Assertions.assertEquals(status2.statusId, taskAgg.statusId)
        Assertions.assertEquals(project.getId(), taskAgg.projectId)

        Assertions.assertThrows(
            IllegalStateException::class.java
        ) {
            taskController.deleteStatus(projectId, status2.statusId)
        }

        ChangeTaskStatus_StatusChanged()
        UpdateTaskAndDeleteStatus_TaskChangedAndStatusDeleted()
        AddExecutors_ExecutorsAddedAndThrowsExceptionIfNotParticipant()
        AddExistingInProjectStatus_ThrowsException()
        ChangeStatusesSequence_SequenceChangedCorrectlyAndThrowsExceptionIfWrongPosition()
    }

    fun ChangeTaskStatus_StatusChanged() {
        val taskUpdatedStatus = taskController.changeStatus(
            projectId,
            taskId,
            defStatusId
        )

        val taskAggUpdatedStatus = taskController.getTask(projectId, taskUpdatedStatus.taskId)
        Assertions.assertNotNull(taskAggUpdatedStatus)
        Assertions.assertEquals("Task", taskAggUpdatedStatus!!.name)
        Assertions.assertEquals(defStatusId, taskAggUpdatedStatus.statusId)
    }

    fun UpdateTaskAndDeleteStatus_TaskChangedAndStatusDeleted() {
        val changedTask = taskController.updateTask(
            projectId,
            taskId,
            "Task new",
            "Task d new"
        )

        val changedTaskAggr = taskController.getTask(projectId, changedTask.taskId)
        Assertions.assertNotNull(changedTaskAggr)
        Assertions.assertEquals("Task new", changedTaskAggr!!.name)
        Assertions.assertEquals("Task d new", changedTaskAggr.description)

        taskController.deleteStatus(projectId, statusId2)
        val taskAgg = taskController.getTaskStatusesAndTasks(projectId)

        Assertions.assertNotNull(taskAgg)
        Assertions.assertEquals(1, taskAgg!!.getStatuses().size)
        Assertions.assertEquals("CREATED", taskAgg.getStatuses()[0].name)
        Assertions.assertEquals(ColorEnum.GREEN, taskAgg.getStatuses()[0].color)
        Assertions.assertEquals(1, taskAgg.getStatuses()[0].position)
        Assertions.assertEquals(1, taskAgg.getTasks().size)
    }

    @Test
    fun createProject_ProjectCreatedWithCorrectFieldsAndCreatedAsParticipant() {
        val owner = createUser("ulyana")
        val project = projectController.createProject(
            "theBestProject",
            owner.userId
        )
        Assertions.assertEquals(1, project.version)
        Assertions.assertEquals("project", project.title)

        val gotProject = projectController.getProject(project.projectId)
        Assertions.assertNotNull(gotProject)

        val ownerInProject = gotProject!!.participants.firstOrNull() { it == owner.userId }
        Assertions.assertNotNull(ownerInProject)
        Assertions.assertEquals("theBestProject", gotProject.projectTitle)
        Assertions.assertEquals(owner.userId, ownerInProject!!)
        Assertions.assertEquals(1, gotProject.participants.size)
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