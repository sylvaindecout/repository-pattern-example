package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.api.AddPlayer
import fr.sdecout.repository.domain.api.ResetPlayerRoster
import fr.sdecout.repository.domain.core.roster.Player
import fr.sdecout.repository.domain.core.roster.PlayerRosterEntry
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
        return users.get(userId)
            .failOnDuplicatePlayer(tournamentId, userId)
            .failOnUnknownTournament(tournamentId)
            .toPlayerIn(tournamentId)
            .also { newEntry -> playerRosterEntries.save(newEntry) }
            .let { Player(it.userId, it.nickname) }
    }

    private fun Users.get(userId: UserId) = find(userId)
        ?: throw DomainExceptions.UserNotFound(userId)

    private fun User.failOnUnknownTournament(tournamentId: TournamentId) = also {
        tournaments.find(tournamentId)
            ?: throw DomainExceptions.TournamentNotFound(tournamentId)
    }

    private fun User.failOnDuplicatePlayer(tournamentId: TournamentId, userId: UserId) = also {
        if (playerRosterEntries.find(tournamentId, userId) != null)
            throw DomainExceptions.DuplicatePlayer(tournamentId, userId)
    }

    private fun User.toPlayerIn(tournamentId: TournamentId) =
        PlayerRosterEntry.from(
            tournamentId = tournamentId,
            userId = id,
            nickname = preferredNickname,
        )

}
