package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.TaskAggregate
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskUpdatedEvent
import ru.quipy.api.TaskStatusChangedEvent
import ru.quipy.api.TaskAssignedToUserEvent
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(
    aggregateClass = TaskAggregate::class, subscriberName = "task-subs-stream"
)
class AnnotationBasedTaskEventsSubscriber(
    private val taskEventsSubscriber: TaskEventsSubscriber
) {

    val logger: Logger = LoggerFactory.getLogger(AnnotationBasedTaskEventsSubscriber::class.java)

    @SubscribeEvent
    fun taskCreatedSubscriber(event: TaskCreatedEvent) {
        logger.info("Task created: {} for project {}", event.taskName, event.projectId)
    }

    @SubscribeEvent
    fun taskUpdatedSubscriber(event: TaskUpdatedEvent) {
        logger.info("Task updated: {} for project {} with new name {}",
            event.taskId, event.projectId, event.newTaskName)
    }

    @SubscribeEvent
    fun taskStatusChangedSubscriber(event: TaskStatusChangedEvent) {
        logger.info("Task status changed: {} for project {} to status {}",
            event.taskId, event.projectId, event.newStatusId)
    }

    @SubscribeEvent
    fun taskAssignedToUserSubscriber(event: TaskAssignedToUserEvent) {
        logger.info("Task assigned: {} for project {} to user {}",
            event.taskId, event.projectId, event.assigneeId)
    }

}
