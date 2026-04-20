package fr.sdecout.repository.domain.api

import fr.sdecout.repository.domain.core.roster.Player
import fr.sdecout.repository.domain.core.tournament.TournamentId
import java.time.LocalDate

fun interface ListPlayers {
    fun listPlayers(tournamentId: TournamentId, requestedOn: () -> LocalDate): List<Player>

    operator fun invoke(tournamentId: TournamentId, requestedOn: () -> LocalDate) =
        listPlayers(tournamentId, requestedOn)
}
