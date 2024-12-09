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
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.addParticipantById
import ru.quipy.logic.create
import ru.quipy.logic.createStatus
import ru.quipy.logic.createUser
import ru.quipy.projections.entity.Project
import ru.quipy.projections.repository.ProjectRepository
import java.util.*
import java.util.concurrent.TimeUnit

@SpringBootTest
@TestPropertySource(locations = ["classpath:application.properties"])
class ProjectProjectionTest {

    companion object {
        private const val timeout_time : Long = 40
    }

    @Autowired
    lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

    @Autowired
    lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var projectRepository: ProjectRepository

    @BeforeEach
    fun setUp() {
        mongoTemplate.dropCollection(Project::class.java)
    }

    @Test
    fun createProject_ProjectCreated() {
        val owner1: UserCreatedEvent = createUser("ulyana")
        val owner2: UserCreatedEvent = createUser("anya")

        val project1: ProjectCreatedEvent = createProject(owner1)
        val project2: ProjectCreatedEvent = createProject(owner2)

        var createdProjects: MutableList<Project>? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            createdProjects = projectRepository.findAll()
            Assertions.assertNotNull(createdProjects)
            Assertions.assertNotEquals(0, createdProjects?.size)
        }

        Assertions.assertNotNull(createdProjects?.find {it.projectId == project1.projectId})
        Assertions.assertNotNull(createdProjects?.find {it.projectId == project2.projectId})
    }

    @Test
    fun createAddParticipant_ParticipantAdded() {
        val owner = createUser("ulyana")
        val user = createUser("anya")
        val project = createProject(owner)

        projectEsService.update(project.projectId) {
            it.addParticipantById(
                user.userId
            )
        }

        var gotProject: Project? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            gotProject = projectRepository.findById(project.projectId).orElse(null)
            Assertions.assertNotNull(gotProject)
        }

        Assertions.assertNotNull(gotProject?.participants)
        Assertions.assertEquals(user.userId, gotProject?.participants?.last())
        Assertions.assertEquals(1, gotProject?.participants?.size)
    }

    @Test
    fun createAddStatus_StatusAdded() {
        val owner = createUser("ulyana")
        val project = createProject(owner)

        val status = projectEsService.update(project.projectId) {
            it.createStatus(
                "status",
                "color"
            )
        }

        var gotProject: Project? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            gotProject = projectRepository.findById(project.projectId).orElse(null)
            Assertions.assertNotNull(gotProject)
        }

        Assertions.assertNotNull(gotProject?.projectStatuses)
        Assertions.assertEquals(status.statusId, gotProject?.projectStatuses?.last()?.id)
        Assertions.assertEquals(1, gotProject?.projectStatuses?.size)
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