package fr.sdecout.repository.infra.driving.rest.player

import jakarta.validation.constraints.PositiveOrZero

data class ScoreUpdateRequestDto(
    @field:PositiveOrZero val score: Int,
)
