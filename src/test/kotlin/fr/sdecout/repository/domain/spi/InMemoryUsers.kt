package fr.sdecout.repository.domain.spi

import fr.sdecout.repository.domain.core.user.User
import fr.sdecout.repository.domain.core.user.UserId

class InMemoryUsers : Users {
    private val values = mutableMapOf<UserId, User>()

    override fun find(id: UserId): User? = values[id]

    override fun findIn(ids: Collection<UserId>): Collection<User> = ids.mapNotNull { find(it) }

    override fun save(user: User) {
        values[user.id] = user
    }

    fun clear() = values.clear()
}
