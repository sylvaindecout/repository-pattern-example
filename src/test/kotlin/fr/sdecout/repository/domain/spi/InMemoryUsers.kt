package fr.sdecout.repository.domain.spi

import fr.sdecout.repository.domain.core.user.City
import fr.sdecout.repository.domain.core.user.Nickname
import fr.sdecout.repository.domain.core.user.User
import fr.sdecout.repository.domain.core.user.UserId
import java.time.LocalDate

class InMemoryUsers : Users {
    private val values = mutableMapOf<UserId, User>()

    override fun find(id: UserId): User? = values[id]

    override fun create(preferredNickname: Nickname, dateOfBirth: LocalDate, city: City): User =
        User(id = UserId.generate(), preferredNickname, dateOfBirth, city).also { user ->
            if (user.id in values) throw IllegalStateException("Duplicate user ID")
            values[user.id] = user
        }

    override fun update(user: User): User = user.also { user ->
        if (user.id !in values) throw IllegalStateException("Unknown user ID")
        values[user.id] = user
    }

    fun save(user: User) {
        values[user.id] = user
    }

    fun clear() = values.clear()
}
