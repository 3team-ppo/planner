package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.TaskAggregate
import ru.quipy.api.TaskAssignedToUserEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusChangedEvent
import ru.quipy.api.TaskUpdatedEvent
import ru.quipy.projections.entity.MongoTask
import ru.quipy.projections.repository.TaskRepository
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.UUID
import javax.annotation.PostConstruct

@Service
class TaskEventsSubscriber(
    private val taskRepository: TaskRepository
) {

    val logger: Logger = LoggerFactory.getLogger(TaskEventsSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TaskAggregate::class, "task-events-subscriber") {

            `when`(TaskCreatedEvent::class) { event ->
                createTaskProject(
                    event.projectId,
                    event.taskId,
                    event.taskName,
                    event.defaultStatusId,
                    event.creatorId
                )
            }

            `when`(TaskUpdatedEvent::class) { event ->
                updateTaskProject(
                    event.projectId,
                    event.taskId,
                    event.newTaskName,
                    event.newPriority,
                    event.newEstimatedTime,
                    event.newAssigneeIds
                    )
            }

            `when`(TaskStatusChangedEvent::class) { event ->
                updateStatusTaskProject(event.projectId, event.taskId, event.newStatusId)
            }

            `when`(TaskAssignedToUserEvent::class) { event ->
                updateAssigneeTaskProject(event.projectId, event.taskId, event.assigneeId)
            }

        }
    }

    fun createTaskProject(
        projectId: UUID,
        taskId: UUID,
        taskName: String,
        defaultStatusId: UUID,
        creatorId: UUID
    ) {
        val task = MongoTask(
            taskId = taskId,
            projectId = projectId,
            taskName = taskName,
            statusId = defaultStatusId,
            creatorId = creatorId
        )

        taskRepository.save(task)
    }

    private fun updateTaskProject(
        projectId: UUID,
        taskId: UUID,
        taskName: String,
        newPriority: Int,
        newEstimatedTime: Int,
        newAssigneeIds: List<UUID>
    ) {
        val actualTask: MongoTask = taskRepository.findById(taskId).get()

        val task = actualTask.copy(
            taskId = taskId,
            projectId = projectId,
            taskName = taskName,
            priority = newPriority,
            estimatedTime = newEstimatedTime,
            assigneeIds = newAssigneeIds
        )

        taskRepository.deleteById(task.taskId)
        taskRepository.save(task)
    }

    private fun updateStatusTaskProject(
        projectId: UUID,
        taskId: UUID,
        newStatusId: UUID
    ) {
        val actualTask: MongoTask = taskRepository.findById(taskId).get()

        val task = actualTask.copy(
            taskId = taskId,
            projectId = projectId,
            statusId = newStatusId
        )

        taskRepository.deleteById(task.taskId)
        taskRepository.save(task)
    }

    private fun updateAssigneeTaskProject(
        projectId: UUID,
        taskId: UUID,
        userId: UUID
    ) {
        val actualTask: MongoTask = taskRepository.findById(taskId).get()

        val task = actualTask.copy(
            taskId = taskId,
            projectId = projectId,
            assigneeIds = actualTask.assigneeIds + userId
        )

        taskRepository.deleteById(task.taskId)
        taskRepository.save(task)
    }
}
