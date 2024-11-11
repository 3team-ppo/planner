package ru.quipy.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.api.*
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.logic.*
import ru.quipy.projections.AnnotationBasedProjectEventsSubscriber
import ru.quipy.projections.AnnotationBasedStatusEventsSubscriber
import ru.quipy.projections.AnnotationBasedTaskEventsSubscriber
import ru.quipy.projections.AnnotationBasedUserEventsSubscriber
import ru.quipy.streams.AggregateEventStreamManager
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Configuration
class EventSourcingLibConfiguration {

    private val logger = LoggerFactory.getLogger(EventSourcingLibConfiguration::class.java)

    @Autowired
    private lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @Autowired
    private lateinit var projectEventSubscriber: AnnotationBasedProjectEventsSubscriber
    @Autowired
    private lateinit var statusEventSubscriber: AnnotationBasedStatusEventsSubscriber
    @Autowired
    private lateinit var taskEventSubscriber: AnnotationBasedTaskEventsSubscriber
    @Autowired
    private lateinit var userEventSubscriber: AnnotationBasedUserEventsSubscriber

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Autowired
    private lateinit var eventStreamManager: AggregateEventStreamManager

    @Bean
    fun projectEsService() = eventSourcingServiceFactory.create<UUID, ProjectAggregate, ProjectAggregateState>()

    @Bean
    fun statusEsService() = eventSourcingServiceFactory.create<UUID, StatusAggregate, StatusAggregateState>()

    @Bean
    fun taskEsService() = eventSourcingServiceFactory.create<UUID, TaskAggregate, TaskAggregateState>()

    @Bean
    fun userEsService() = eventSourcingServiceFactory.create<UUID, UserAggregate, UserAggregateState>()

    @PostConstruct
    fun init() {
        subscriptionsManager.subscribe<ProjectAggregate>(projectEventSubscriber)

        subscriptionsManager.subscribe<StatusAggregate>(statusEventSubscriber)

        subscriptionsManager.subscribe<TaskAggregate>(taskEventSubscriber)

        subscriptionsManager.subscribe<UserAggregate>(userEventSubscriber)

        eventStreamManager.maintenance {
            onRecordHandledSuccessfully { streamName, eventName ->
                logger.info("Stream $streamName successfully processed record of $eventName")
            }

            onBatchRead { streamName, batchSize ->
                logger.info("Stream $streamName read batch size: $batchSize")
            }
        }
    }
}
