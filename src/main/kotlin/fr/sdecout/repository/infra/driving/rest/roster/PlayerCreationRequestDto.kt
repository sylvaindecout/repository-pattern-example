package fr.sdecout.repository.infra.driving.rest.roster

import fr.sdecout.repository.domain.core.user.UserId
import jakarta.validation.constraints.NotBlank

data class PlayerCreationRequestDto(
    @field:NotBlank val userId: String,
) {
    fun toDomain() = UserId.from(userId)
}
