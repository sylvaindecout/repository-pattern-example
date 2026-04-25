package fr.sdecout.repository.domain.core.roster

import fr.sdecout.repository.domain.core.tournament.RosterSize
import fr.sdecout.repository.domain.core.tournament.RosterSize.Companion.players
import fr.sdecout.repository.domain.core.tournament.Tournament
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.Age
import fr.sdecout.repository.domain.core.user.UserId
import java.util.Objects.hash

/**
 * Issue: We decided to aggregate players because of the roster size check. How is it relevant when you update a score?
 * The player roster and the scoreboard evolve at different times, with distinct sets of constraints.
 */
class PlayerRoster private constructor(
    val tournamentId: TournamentId,
    val maxPlayerRosterSize: RosterSize,
    val minimumAge: Age?,
    private val playersById: Map<UserId, Player> = emptyMap(),
    val pendingPlayers: Set<Player> = emptySet(),
    val pendingScoreUpdates: Set<Player> = emptySet(),
) {
    val size: RosterSize = playersById.size.players

    init {
        check(playersById.values.groupBy { it.nickname }.size == playersById.size) {
            "Player roster must not include duplicate nicknames"
        }
        check(size <= maxPlayerRosterSize) {
            "Player roster must not have more player ($size) than limit ($maxPlayerRosterSize)"
        }
    }

    companion object {
        fun Tournament.toPlayerRoster(vararg players: Player) = PlayerRoster(
            tournamentId = id,
            maxPlayerRosterSize = maxPlayerRosterSize,
            minimumAge = minimumAge,
            playersById = players.toList().associateBy { it.userId },
        )

        fun from(
            tournamentId: TournamentId,
            maxPlayerRosterSize: RosterSize,
            minimumAge: Age?,
            players: Collection<Player>,
        ) = PlayerRoster(
            tournamentId,
            maxPlayerRosterSize,
            minimumAge,
            playersById = players.associateBy { it.userId },
        )
    }

    val isFull: Boolean by lazy { size >= maxPlayerRosterSize }

    val players get(): Collection<Player> = playersById.values

    operator fun get(userId: UserId): Player? = playersById[userId]

    operator fun contains(userId: UserId): Boolean = userId in playersById

    fun add(player: Player): PlayerRoster = player.also {
        require(it.userId !in playersById) {
            "Player roster must not include duplicate users"
        }
    }.let {
        PlayerRoster(
            tournamentId = tournamentId,
            maxPlayerRosterSize = maxPlayerRosterSize,
            minimumAge = minimumAge,
            playersById = playersById + (it.userId to it),
            pendingPlayers = pendingPlayers + it,
            pendingScoreUpdates = pendingScoreUpdates,
        )
    }

    fun update(player: Player): PlayerRoster = player.also {
        require(it.userId in playersById) {
            "Player roster must include updated user"
        }
    }.let {
        PlayerRoster(
            tournamentId = tournamentId,
            maxPlayerRosterSize = maxPlayerRosterSize,
            minimumAge = minimumAge,
            playersById = playersById + (it.userId to it),
            pendingPlayers = pendingPlayers,
            pendingScoreUpdates = pendingScoreUpdates + it,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerRoster

        if (tournamentId != other.tournamentId) return false
        if (maxPlayerRosterSize != other.maxPlayerRosterSize) return false
        if (minimumAge != other.minimumAge) return false
        if (playersById != other.playersById) return false
        if (pendingPlayers != other.pendingPlayers) return false
        if (pendingScoreUpdates != other.pendingScoreUpdates) return false

        return true
    }

    override fun hashCode(): Int =
        hash(tournamentId, maxPlayerRosterSize, minimumAge, playersById, pendingPlayers, pendingScoreUpdates)

    override fun toString(): String =
        "PlayerRoster(tournamentId=$tournamentId, maxPlayerRosterSize=$maxPlayerRosterSize, minimumAge=$minimumAge, players=$players, pendingPlayers=$pendingPlayers), pendingScoreUpdates=$pendingScoreUpdates)"
}
