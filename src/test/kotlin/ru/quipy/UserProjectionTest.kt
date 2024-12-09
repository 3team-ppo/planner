package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.TestPropertySource
import org.testcontainers.shaded.org.awaitility.Awaitility
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.createUser
import ru.quipy.logic.updateUser
import ru.quipy.projections.entity.User
import ru.quipy.projections.repository.UserRepository
import java.util.*
import java.util.concurrent.TimeUnit

@SpringBootTest
@TestPropertySource(locations = ["classpath:application.properties"])
class UserProjectionTest {

    companion object {
        private const val timeout_time : Long = 40
    }

    @Autowired
    lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        mongoTemplate.dropCollection(User::class.java)
    }

    @Test
    fun createUser_UserCreated() {
        val owner1 = createUser("ulyana")
        val owner2 = createUser("anya")

        var createdUsers: MutableList<User>? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            createdUsers = userRepository.findAll()
            Assertions.assertNotNull(createdUsers)
            Assertions.assertNotEquals(0, createdUsers?.size)
        }

        Assertions.assertNotNull(createdUsers?.find {it.userId == owner1.userId})
        Assertions.assertNotNull(createdUsers?.find {it.userId == owner2.userId})
    }

    @Test
    fun changeUser_UserChanged() {
        val owner = createUser("ulyana")

        val updatedUser = userEsService.update(owner.userId) {
            it.updateUser(
                "anya",
                "anya",
                "password",
            )
        }

        var gotUser: User? = null
        Awaitility.await().timeout(timeout_time, TimeUnit.SECONDS).untilAsserted {
            gotUser = userRepository.findById(owner.userId).orElse(null)
            Assertions.assertNotNull(gotUser)
        }

        Assertions.assertEquals(updatedUser.newName, gotUser?.userName)
        Assertions.assertEquals(updatedUser.newLogin, gotUser?.login)
        Assertions.assertEquals(updatedUser.newPassword, gotUser?.password)
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