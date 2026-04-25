package fr.sdecout.repository.infra.driving.rest.player

import fr.sdecout.repository.domain.core.player.PlayerOverview
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class PlayerDto(
    @field:NotBlank val id: String,
    @field:NotBlank val nickname: String,
    @field:Positive val age: Int,
    @field:NotBlank val city: String,
) {
    companion object {
        fun PlayerOverview.toDto(): PlayerDto = PlayerDto(
            id = userId.value,
            nickname = nickname.value,
            age = age.value,
            city = city.name,
        )
    }
}
