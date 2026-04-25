package fr.sdecout.repository.domain.core.scoreboard

import fr.sdecout.repository.domain.core.scoreboard.Score.Companion.points
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.UserId
import java.util.Objects.hash

class ScoreboardEntry private constructor(
    val tournamentId: TournamentId,
    val userId: UserId,
    val score: Score,
) {
    companion object {
        fun new(tournamentId: TournamentId, userId: UserId) =
            ScoreboardEntry(tournamentId, userId, 0.points)

        fun from(tournamentId: TournamentId, userId: UserId, score: Score) =
            ScoreboardEntry(tournamentId, userId, score)
    }

    fun update(score: Score) = ScoreboardEntry(
        tournamentId = tournamentId,
        userId = userId,
        score = score,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScoreboardEntry

        if (tournamentId != other.tournamentId) return false
        if (userId != other.userId) return false
        if (score != other.score) return false

        return true
    }

    override fun hashCode(): Int = hash(tournamentId, userId, score)

    override fun toString(): String = "ScoreboardEntry(tournamentId=$tournamentId, userId=$userId, score=$score)"
}
