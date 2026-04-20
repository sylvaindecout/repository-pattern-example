package fr.sdecout.repository.domain.api

import fr.sdecout.repository.domain.core.tournament.TournamentId

fun interface ResetPlayerRoster {
    fun resetPlayerRoster(tournamentId: TournamentId)

    operator fun invoke(tournamentId: TournamentId) = resetPlayerRoster(tournamentId)
}
