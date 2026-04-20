package fr.sdecout.repository.infra.driving.rest.tournament

import fr.sdecout.repository.domain.core.tournament.RosterSize.Companion.players
import fr.sdecout.repository.domain.core.tournament.Tournament
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.tournament.TournamentName
import fr.sdecout.repository.domain.core.user.Age.Companion.yearsOld
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class TournamentDto(
    @field:NotBlank val id: String,
    @field:NotBlank val name: String,
    @field:Positive val maxPlayerRosterSize: Int,
    @field:Positive val minimumAge: Int?,
) {
    companion object {
        fun Tournament.toDto(): TournamentDto = TournamentDto(
            id = id.value,
            name = name.value,
            maxPlayerRosterSize = maxPlayerRosterSize.value,
            minimumAge = minimumAge?.value,
        )
    }

    fun toDomain(): Tournament = Tournament(
        id = TournamentId.from(id),
        name = TournamentName.from(name),
        maxPlayerRosterSize = maxPlayerRosterSize.players,
        minimumAge = minimumAge?.yearsOld,
    )
}
