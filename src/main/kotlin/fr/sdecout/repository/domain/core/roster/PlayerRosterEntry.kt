package fr.sdecout.repository.domain.core.roster

import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.Nickname
import fr.sdecout.repository.domain.core.user.UserId
import java.util.Objects.hash

class PlayerRosterEntry private constructor(
    val tournamentId: TournamentId,
    val userId: UserId,
    val nickname: Nickname,
) {
    companion object {
        fun from(tournamentId: TournamentId, userId: UserId, nickname: Nickname) =
            PlayerRosterEntry(tournamentId, userId, nickname)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerRosterEntry

        if (tournamentId != other.tournamentId) return false
        if (userId != other.userId) return false
        if (nickname != other.nickname) return false

        return true
    }

    override fun hashCode(): Int = hash(tournamentId, userId, nickname)

    override fun toString(): String = "PlayerRosterEntry(tournamentId=$tournamentId, userId=$userId, nickname=$nickname)"
}
