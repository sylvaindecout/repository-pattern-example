package fr.sdecout.repository.domain.spi

import fr.sdecout.repository.domain.core.user.User
import fr.sdecout.repository.domain.core.user.UserId

interface Users {
    fun find(id: UserId): User?
    fun save(user: User)
}
