// package ru.quipy.projections
//
// import org.slf4j.Logger
// import org.slf4j.LoggerFactory
// import org.springframework.beans.factory.annotation.Autowired
// import org.springframework.stereotype.Service
// import ru.quipy.api.StatusCreatedEvent
// import ru.quipy.api.StatusUpdatedEvent
// import ru.quipy.streams.AggregateSubscriptionsManager
// import javax.annotation.PostConstruct
//
// @Service
// class StatusEventsSubscriber {
//
//     val logger: Logger = LoggerFactory.getLogger(StatusEventsSubscriber::class.java)
//
//     @Autowired
//     lateinit var subscriptionsManager: AggregateSubscriptionsManager
//
//     @PostConstruct
//     fun init() {
//         subscriptionsManager.createSubscriber(StatusAggregate::class, "status-events-subscriber") {
//
//             `when`(StatusCreatedEvent::class) { event ->
//                 logger.info("Status created: {} with color {}", event.statusName, event.color)
//             }
//
//             `when`(StatusUpdatedEvent::class) { event ->
//                 logger.info("Status updated: {} to new name {} and color {}",
//                     event.statusId, event.newStatusName, event.newColor)
//             }
//         }
//     }
// }
