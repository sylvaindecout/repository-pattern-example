package fr.sdecout.repository.domain.core.user

import fr.sdecout.repository.domain.core.user.Age.Companion.toAge
import java.time.LocalDate
import java.util.*

class User(
    val id: UserId,
    val preferredNickname: Nickname,
    val dateOfBirth: LocalDate,
    val city: City,
) {
    companion object

    fun age(computedOn: () -> LocalDate): Age = dateOfBirth.toAge(computedOn)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (preferredNickname != other.preferredNickname) return false
        if (dateOfBirth != other.dateOfBirth) return false
        if (city != other.city) return false

        return true
    }

    override fun hashCode(): Int = Objects.hash(id, preferredNickname, dateOfBirth, city)

    override fun toString(): String =
        "Player(id=$id, preferredNickname=$preferredNickname, dateOfBirth=$dateOfBirth, city=$city)"
}
