package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.api.UserUpdatedEvent
import ru.quipy.projections.entity.Project
import ru.quipy.projections.entity.User
import ru.quipy.projections.repository.UserRepository
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.UUID
import javax.annotation.PostConstruct

@Service
class UserEventsSubscriber(
    private val userRepository: UserRepository
) {

    val logger: Logger = LoggerFactory.getLogger(UserEventsSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(UserAggregate::class, "user-events-subscriber") {

            `when`(UserCreatedEvent::class) { event ->
                createUser(event.userId, event.userName, event.login, event.password)
            }

            `when`(UserUpdatedEvent::class) { event ->
                updateUser(event.userId, event.newName, event.newLogin, event.newPassword)
            }
        }
    }

    private fun updateUser(userId: UUID, newName: String, newLogin: String, newPassword: String) {
        val actualUser = userRepository.findById(userId).get()

        val user = actualUser.copy(
            userName = newName,
            login = newLogin,
            password = newPassword
        )

        userRepository.deleteById(userId)
        userRepository.save(user)
    }

    private fun createUser(userId: UUID, userName: String, login: String, password: String) {
        val user = User(
            userId = userId,
            userName = userName,
            login = login,
            password = password
        )

        userRepository.save(user)
    }
}
