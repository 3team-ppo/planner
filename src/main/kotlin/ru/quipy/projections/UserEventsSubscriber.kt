package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.UserUpdatedEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class UserEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(UserEventsSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(UserAggregate::class, "user-events-subscriber") {

            `when`(UserCreatedEvent::class) { event ->
                logger.info("User created: ID {} with name {} and login {}", event.userId, event.userName, event.login)
            }

            `when`(UserUpdatedEvent::class) { event ->
                logger.info("User updated: ID {} with new name {} and login {}", event.userId, event.newName, event.newLogin)
            }
        }
    }
}
