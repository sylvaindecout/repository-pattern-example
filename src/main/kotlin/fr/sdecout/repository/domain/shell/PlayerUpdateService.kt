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

    override fun addPlayer(tournamentId: TournamentId, userId: UserId, addedOn: () -> LocalDate): Player {
        val user = users.get(userId)
        val entries = playerRosterEntries.findAll(tournamentId)
        val tournament = tournaments.get(tournamentId)
        if (entries.size.players >= tournament.maxPlayerRosterSize)
            throw DomainExceptions.FullPlayerRoster(tournamentId)
        val newEntry = user.toPlayerIn(entries, tournamentId)
        if (entries.any { it.userId == newEntry.userId })
            throw DomainExceptions.DuplicatePlayer(newEntry.tournamentId, newEntry.userId)
        playerRosterEntries.save(newEntry)
        return Player(newEntry.userId, newEntry.nickname)
    }

    private fun Users.get(userId: UserId) = find(userId)
        ?: throw DomainExceptions.UserNotFound(userId)

    private fun Tournaments.get(tournamentId: TournamentId) = find(tournamentId)
        ?: throw DomainExceptions.TournamentNotFound(tournamentId)

    private fun User.toPlayerIn(playerRoster: List<PlayerRosterEntry>, tournamentId: TournamentId) =
        PlayerRosterEntry.from(
            tournamentId = tournamentId,
            userId = id,
            nickname = playerRoster.availableNicknameClosestTo(preferredNickname),
        )

}
