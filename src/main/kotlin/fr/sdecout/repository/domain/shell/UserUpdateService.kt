package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.api.UpsertUser
import fr.sdecout.repository.domain.core.user.User
import fr.sdecout.repository.domain.spi.Users

class UserUpdateService(private val users: Users) : UpsertUser {

    override fun upsert(user: User) = users.save(user)

}
