package fr.sdecout.repository.domain.api

import fr.sdecout.repository.domain.core.user.User
import fr.sdecout.repository.domain.core.user.UserId

fun interface FindUser {
    fun findUser(id: UserId): User?

    operator fun invoke(id: UserId) = findUser(id)
}

