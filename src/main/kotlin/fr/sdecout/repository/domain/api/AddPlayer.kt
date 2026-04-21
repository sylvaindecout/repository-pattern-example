package fr.sdecout.repository.domain.api

import fr.sdecout.repository.domain.core.player.PlayerOverview
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.UserId
import java.time.LocalDate

fun interface AddPlayer {
    fun addPlayer(tournamentId: TournamentId, userId: UserId, addedOn: () -> LocalDate): PlayerOverview

    operator fun invoke(tournamentId: TournamentId, userId: UserId, addedOn: () -> LocalDate) =
        addPlayer(tournamentId, userId, addedOn)
}
