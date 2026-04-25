package fr.sdecout.repository.domain.api

import fr.sdecout.repository.domain.core.player.PlayerOverview
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.UserId
import java.time.LocalDate

fun interface FindPlayer {
    fun findPlayer(tournamentId: TournamentId, userId: UserId, requestedOn: () -> LocalDate): PlayerOverview?

    operator fun invoke(tournamentId: TournamentId, userId: UserId, requestedOn: () -> LocalDate) =
        findPlayer(tournamentId, userId, requestedOn)
}
