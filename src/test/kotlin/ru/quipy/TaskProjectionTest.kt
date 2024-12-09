package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.TestPropertySource
import org.testcontainers.shaded.org.awaitility.Awaitility
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.TaskAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.TaskAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.addParticipantById
import ru.quipy.logic.addTask
import ru.quipy.logic.assignTaskToUser
import ru.quipy.logic.changeTaskStatus
import ru.quipy.logic.create
import ru.quipy.logic.createStatus
import ru.quipy.logic.createUser
import ru.quipy.projections.entity.MongoTask
import ru.quipy.projections.entity.Project
import ru.quipy.projections.entity.User
import ru.quipy.projections.repository.ProjectRepository
import ru.quipy.projections.repository.TaskRepository
import java.util.*
import java.util.concurrent.TimeUnit

@SpringBootTest
@TestPropertySource(locations = ["classpath:application.properties"])
class TaskProjectionTest {

    companion object {
        private const val timeout_time : Long = 40
    }

    @Autowired
    private lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

    @Autowired
    lateinit var taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>

    @Autowired
    lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var taskRepository: TaskRepository

    @BeforeEach
    fun setUp() {
        mongoTemplate.dropCollection(Project::class.java)
        mongoTemplate.dropCollection(User::class.java)
        mongoTemplate.dropCollection(MongoTask::class.java)
    }

    @Test
    fun createTask_taskCreated() {
        val owner = createUser("ulyana")
        val project = createProject(owner)

        val addedTask = projectEsService.update(project.projectId) {
            it.addTask(
                "lab_7",
                owner.userId
            )
        }

        val task =  taskEsService.create {
            it.create(
                project.projectId,
                addedTask.taskId,
                "lab_7",
                owner.userId,
                project.defaultStatusId)
        }

        var gotTask: MongoTask? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            gotTask = taskRepository.findById(task.taskId).orElse(null)
            Assertions.assertNotNull(gotTask)
        }

        val gotProject = projectRepository.findById(project.projectId).orElse(null)
        Assertions.assertNotNull(gotProject)
        Assertions.assertNotNull(gotProject.tasks.firstOrNull())
        Assertions.assertEquals(task.taskId, gotProject.tasks.firstOrNull()?.id)
        Assertions.assertEquals(task.taskName, gotTask?.taskName)
        Assertions.assertEquals(task.defaultStatusId, gotTask?.statusId)
    }

    @Test
    fun changeTaskStatus_StatusChanged() {
        val owner = createUser("ulyana")
        val project = createProject(owner)
        val task =  taskEsService.create {
            it.create(
                project.projectId,
                UUID.randomUUID(),
                "lab_7",
                owner.userId,
                project.defaultStatusId)
        }

        val status = projectEsService.update(project.projectId) {
            it.createStatus(
                "status",
                "color"
            )
        }

        val updatedTask = taskEsService.update(task.taskId) {
            it.changeTaskStatus(
                status.statusId,
                project.projectId
            )
        }

        var gotTask: MongoTask? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            gotTask = taskRepository.findById(task.taskId).orElse(null)
            Assertions.assertNotNull(gotTask)
        }

        Assertions.assertEquals(task.taskName, gotTask?.taskName)
        Assertions.assertEquals(updatedTask.newStatusId, gotTask?.statusId)
    }


    @Test
    fun assignUser_UserAssigned() {
        val owner = createUser("ulyana")
        val user = createUser("anya")
        val project = createProject(owner)

        projectEsService.update(project.projectId) {
            it.addParticipantById(
                user.userId
            )
        }

        val task =  taskEsService.create {
            it.create(
                project.projectId,
                UUID.randomUUID(),
                "lab_7",
                owner.userId,
                project.defaultStatusId)
        }

        val updatedTask = taskEsService.update(task.taskId) {
            it.assignTaskToUser(
                user.userId,
                project.projectId
            )
        }

        var gotTask: MongoTask? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            gotTask = taskRepository.findById(task.taskId).orElse(null)
            Assertions.assertNotNull(gotTask)
        }

        Assertions.assertEquals(task.taskName, gotTask?.taskName)
        Assertions.assertEquals(updatedTask.assigneeId, gotTask?.assigneeIds?.firstOrNull())
    }

    private fun createProject(owner: UserCreatedEvent): ProjectCreatedEvent {
        return projectEsService.create {
            it.create(
                "theBestProject",
                owner.userId
            )
        }
    }

    private fun createUser(name: String): UserCreatedEvent {
        return userEsService.create {
            it.createUser(
                name,
                name,
                "1234568"
            )
        }
    }
}