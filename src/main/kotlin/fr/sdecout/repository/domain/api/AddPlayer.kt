package fr.sdecout.repository.domain.api

import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.UserId
import fr.sdecout.repository.domain.core.roster.Player
import java.time.LocalDate

fun interface AddPlayer {
    fun addPlayer(tournamentId: TournamentId, userId: UserId, addedOn: () -> LocalDate): Player

    operator fun invoke(tournamentId: TournamentId, userId: UserId, addedOn: () -> LocalDate) =
        addPlayer(tournamentId, userId, addedOn)
}
