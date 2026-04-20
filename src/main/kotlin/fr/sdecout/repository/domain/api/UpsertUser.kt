package fr.sdecout.repository.domain.api

import fr.sdecout.repository.domain.core.user.User

fun interface UpsertUser {
    fun upsert(user: User)

    operator fun invoke(user: User) = upsert(user)
}
