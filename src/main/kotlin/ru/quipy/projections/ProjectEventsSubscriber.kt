package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.ParticipantAddedEvent
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.StatusCreatedEvent
import ru.quipy.api.StatusDeletedEvent
import ru.quipy.api.StatusUpdatedEvent
import ru.quipy.api.TagCreatedEvent
import ru.quipy.api.TaskAddedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.projections.entity.MongoTask
import ru.quipy.projections.entity.Project
import ru.quipy.projections.entity.Status
import ru.quipy.projections.entity.Task
import ru.quipy.projections.repository.ProjectRepository
import ru.quipy.projections.repository.TaskRepository
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.UUID
import javax.annotation.PostConstruct

@Service
class ProjectEventsSubscriber(
    private val projectRepository: ProjectRepository
) {

    val logger: Logger = LoggerFactory.getLogger(ProjectEventsSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "some-meaningful-name") {
            `when`(ProjectCreatedEvent::class) { event ->
                createProject(event.projectId, event.title, event.creatorId, event.defaultStatusId)
            }

            `when`(StatusCreatedEvent::class) { event ->
                createStatusProject(event.projectId, event.statusId, event.statusName, event.color)
            }

            `when`(StatusUpdatedEvent::class) { event ->
                updateStatusProject(event.projectId, event.statusId, event.newStatusName, event.newColor)
            }

            `when`(StatusDeletedEvent::class) { event ->
                deleteStatusProject(event.projectId, event.statusId)
            }

            `when`(TaskAddedEvent::class) { event ->
                createTaskProject(event.projectId, event.taskId, event.taskName, event.defaultStatusId, event.creatorId)
            }

            `when`(TagCreatedEvent::class) { event ->
                createTagProject(event.projectId, event.tagId)
            }

            `when`(ParticipantAddedEvent::class) { event ->
                participantAddedToProject(event.projectId, event.userId)
            }
        }
    }

    private fun createProject(id: UUID, name: String, creatorId: UUID, defaultStatusId: UUID) {
        val status = Status(defaultStatusId, "CREATED", "BLUE")
        val project = Project(
            projectId = id,
            projectTitle = name,
            creatorId = creatorId,
            defaultStatus = status
        )
        projectRepository.save(project)
    }

    private fun createStatusProject(projectId: UUID, statusId: UUID, name: String, color: String) {
        val status = Status(statusId, name, color)
        val actualProject: Project = projectRepository.findById(projectId).get()
        val project = actualProject.copy(
            projectStatuses = actualProject.projectStatuses + status,
            updatedAt = System.currentTimeMillis()
        )

        projectRepository.deleteById(projectId)
        projectRepository.save(project)
    }

    private fun updateStatusProject(projectId: UUID, statusId: UUID, name: String, color: String) {
        val updStatus = Status(statusId, name, color)
        val actualProject: Project = projectRepository.findById(projectId).get()

        val updatedStatuses = actualProject.projectStatuses.map { status ->
            if (status.id == statusId) {
                updStatus
            } else {
                status
            }
        }

        val project = actualProject.copy(
            projectStatuses = updatedStatuses,
            updatedAt = System.currentTimeMillis()
        )

        projectRepository.deleteById(projectId)
        projectRepository.save(project)
    }

    private fun deleteStatusProject(projectId: UUID, statusId: UUID) {
        val actualProject: Project = projectRepository.findById(projectId).get()

        val updatedStatuses = actualProject.projectStatuses.filter { status ->
            status.id != statusId
        }

        val project = actualProject.copy(
            projectStatuses = updatedStatuses,
            updatedAt = System.currentTimeMillis()
        )

        projectRepository.deleteById(projectId)
        projectRepository.save(project)
    }

    private fun createTaskProject(projectId: UUID, taskId: UUID, name: String, defaultStatusId: UUID, creatorId: UUID) {
        val task = Task(taskId, name, defaultStatusId)
        val actualProject: Project = projectRepository.findById(projectId).get()
        val project = actualProject.copy(
            tasks = actualProject.tasks + task,
            updatedAt = System.currentTimeMillis()
        )

        projectRepository.deleteById(projectId)
        projectRepository.save(project)
    }

    private fun createTagProject(projectId: UUID, tagId: UUID) {
        val actualProject: Project = projectRepository.findById(projectId).get()
        val project = actualProject.copy(
            projectTags = actualProject.projectTags + tagId,
            updatedAt = System.currentTimeMillis()
        )

        projectRepository.deleteById(projectId)
        projectRepository.save(project)
    }

    private fun participantAddedToProject(projectId: UUID, userId: UUID) {
        val actualProject: Project = projectRepository.findById(projectId).get()
        val project = actualProject.copy(
            participants = actualProject.participants + userId,
            updatedAt = System.currentTimeMillis()
        )

        projectRepository.deleteById(projectId)
        projectRepository.save(project)
    }
}