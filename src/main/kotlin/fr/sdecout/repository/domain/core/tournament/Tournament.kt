package fr.sdecout.repository.domain.core.tournament

import fr.sdecout.repository.domain.core.user.Age

data class Tournament(
    val id: TournamentId,
    val name: TournamentName,
    val maxPlayerRosterSize: RosterSize,
    val minimumAge: Age?,
) {
    companion object
}
