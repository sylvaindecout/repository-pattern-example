package fr.sdecout.repository.domain.spi

import fr.sdecout.repository.domain.core.user.City
import fr.sdecout.repository.domain.core.user.Nickname
import fr.sdecout.repository.domain.core.user.User
import fr.sdecout.repository.domain.core.user.UserId
import java.time.LocalDate

interface Users {
    fun find(id: UserId): User?
    fun create(preferredNickname: Nickname, dateOfBirth: LocalDate, city: City): User
    fun update(user: User): User
}
