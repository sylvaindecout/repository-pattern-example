package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.api.AddPlayer
import fr.sdecout.repository.domain.api.ResetPlayerRoster
import fr.sdecout.repository.domain.core.roster.Player
import fr.sdecout.repository.domain.core.roster.PlayerRosterEntry
import fr.sdecout.repository.domain.core.roster.availableNicknameClosestTo
import fr.sdecout.repository.domain.core.tournament.RosterSize.Companion.players
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.User
import fr.sdecout.repository.domain.core.user.UserId
import fr.sdecout.repository.domain.spi.PlayerRosterEntries
import fr.sdecout.repository.domain.spi.Tournaments
import fr.sdecout.repository.domain.spi.Users
import java.time.LocalDate

class PlayerUpdateService(
    private val users: Users,
    private val tournaments: Tournaments,
    private val playerRosterEntries: PlayerRosterEntries,
) : ResetPlayerRoster, AddPlayer {

    override fun resetPlayerRoster(tournamentId: TournamentId) {
        playerRosterEntries.removeAll(tournamentId)
    }

    /**
     * Two features have been added:
     * * Reject the player if the roster is already full
     * * Generate a unique nickname in case the user's preferred nickname is already used
     *
     * Issue: The whole roster needs to be loaded.
     *
     * Issue: Concurrent updates may lead to broken rules.
     */
    override fun addPlayer(tournamentId: TournamentId, userId: UserId, addedOn: () -> LocalDate): Player {
        val user = users.get(userId)
        val entries = playerRosterEntries.findAll(tournamentId)
            .failOnDuplicatePlayer(tournamentId, userId)
        val tournament = tournaments.get(tournamentId)
        if (entries.size.players >= tournament.maxPlayerRosterSize)
            throw DomainExceptions.FullPlayerRoster(tournamentId)
        return user.toPlayerIn(entries, tournamentId)
            .also { newEntry -> playerRosterEntries.save(newEntry) }
            .let { Player(it.userId, it.nickname) }
    }

    private fun Users.get(userId: UserId) = find(userId)
        ?: throw DomainExceptions.UserNotFound(userId)

    private fun Tournaments.get(tournamentId: TournamentId) = find(tournamentId)
        ?: throw DomainExceptions.TournamentNotFound(tournamentId)

    private fun List<PlayerRosterEntry>.failOnDuplicatePlayer(tournamentId: TournamentId, userId: UserId) = also {
        if (any { it.userId == userId })
            throw DomainExceptions.DuplicatePlayer(tournamentId, userId)
    }

    private fun User.toPlayerIn(playerRoster: List<PlayerRosterEntry>, tournamentId: TournamentId) =
        PlayerRosterEntry.from(
            tournamentId = tournamentId,
            userId = id,
            nickname = playerRoster.availableNicknameClosestTo(preferredNickname),
        )

}
