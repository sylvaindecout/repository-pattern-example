package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.api.UpsertUser
import fr.sdecout.repository.domain.core.user.User
import fr.sdecout.repository.domain.spi.Users

class UserUpdateService(private val users: Users) : UpsertUser {

    /**
     * Read before write. Is it transactional? (no -> inconsistency is bound to happen)
     */
    override fun upsert(user: User) {
        if (users.find(user.id) == null) users.create(user.preferredNickname, user.dateOfBirth, user.city)
        else users.update(User(user.id, user.preferredNickname, user.dateOfBirth, user.city))
    }

}
