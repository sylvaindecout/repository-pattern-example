package fr.sdecout.repository.infra.driving.rest.roster

import fr.sdecout.repository.domain.core.roster.Player
import jakarta.validation.constraints.NotBlank

data class PlayerDto(
    @field:NotBlank val id: String,
    @field:NotBlank val nickname: String,
) {
    companion object {
        fun Player.toDto(): PlayerDto = PlayerDto(
            id = userId.value,
            nickname = nickname.value,
        )
    }
}
