package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.api.FindUser
import fr.sdecout.repository.domain.core.user.User
import fr.sdecout.repository.domain.core.user.UserId
import fr.sdecout.repository.domain.spi.Users

class UserAccessService(private val users: Users) : FindUser {

    override fun findUser(id: UserId): User? = users.find(id)

}
