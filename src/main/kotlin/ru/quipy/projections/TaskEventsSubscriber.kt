package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.TaskAggregate
import ru.quipy.api.TaskStatusChangedEvent
import ru.quipy.api.TaskAssignedToUserEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class TaskEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(TaskEventsSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TaskAggregate::class, "task-events-subscriber") {

            `when`(TaskStatusChangedEvent::class) { event ->
                logger.info("Task status changed: Task ID {} in project {} to new status {}",
                    event.taskId, event.projectId, event.newStatusId)
            }

            `when`(TaskAssignedToUserEvent::class) { event ->
                logger.info("Task assigned to user: Task ID {} in project {} assigned to user {}",
                    event.taskId, event.projectId, event.assigneeId)
            }

        }
    }
}
