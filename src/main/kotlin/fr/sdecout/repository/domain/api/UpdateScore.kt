package fr.sdecout.repository.domain.api

import fr.sdecout.repository.domain.core.scoreboard.Score
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.UserId

fun interface UpdateScore {
    fun updateScore(tournamentId: TournamentId, userId: UserId, score: Score)

    operator fun invoke(tournamentId: TournamentId, userId: UserId, score: Score) =
        updateScore(tournamentId, userId, score)
}
