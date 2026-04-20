package fr.sdecout.repository.infra.driving.rest.user

import fr.sdecout.repository.domain.core.user.City
import fr.sdecout.repository.domain.core.user.Nickname
import fr.sdecout.repository.domain.core.user.User
import fr.sdecout.repository.domain.core.user.UserId
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

data class UserDto(
    @field:NotBlank val id: String,
    @field:NotBlank val preferredNickname: String,
    val dateOfBirth: LocalDate,
    @field:NotBlank val city: String,
) {
    companion object {
        fun User.toDto(): UserDto = UserDto(
            id = id.value,
            preferredNickname = preferredNickname.value,
            dateOfBirth = dateOfBirth,
            city = city.name,
        )
    }

    fun toDomain(): User = User(
        id = UserId.from(id),
        preferredNickname = Nickname.from(preferredNickname),
        dateOfBirth = dateOfBirth,
        city = City.from(city),
    )
}
