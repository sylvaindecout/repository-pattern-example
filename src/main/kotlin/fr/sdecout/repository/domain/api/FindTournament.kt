package fr.sdecout.repository.domain.api

import fr.sdecout.repository.domain.core.tournament.Tournament
import fr.sdecout.repository.domain.core.tournament.TournamentId

fun interface FindTournament {
    fun findTournament(tournamentId: TournamentId): Tournament?

    operator fun invoke(tournamentId: TournamentId) = findTournament(tournamentId)
}
