package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.StatusAggregate
import ru.quipy.api.StatusCreatedEvent
import ru.quipy.api.StatusUpdatedEvent
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(
    aggregateClass = StatusAggregate::class, subscriberName = "status-events-subscriber"
)
class AnnotationBasedStatusEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(AnnotationBasedStatusEventsSubscriber::class.java)

    @SubscribeEvent
    fun statusCreatedSubscriber(event: StatusCreatedEvent) {
        logger.info("Status created: {} with color {}", event.statusName, event.color)
    }

    @SubscribeEvent
    fun statusUpdatedSubscriber(event: StatusUpdatedEvent) {
        logger.info("Status updated: {} with new color {}", event.newStatusName, event.newColor)
    }
}
