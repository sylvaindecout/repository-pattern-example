package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.api.AddPlayer
import fr.sdecout.repository.domain.api.ResetPlayerRoster
import fr.sdecout.repository.domain.core.roster.Player
import fr.sdecout.repository.domain.core.roster.PlayerRoster
import fr.sdecout.repository.domain.core.roster.PlayerRoster.Companion.toPlayerRoster
import fr.sdecout.repository.domain.core.roster.availableNicknameClosestTo
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.User
import fr.sdecout.repository.domain.core.user.UserId
import fr.sdecout.repository.domain.spi.PlayerRosters
import fr.sdecout.repository.domain.spi.Tournaments
import fr.sdecout.repository.domain.spi.Users
import java.time.LocalDate

class PlayerUpdateService(
    private val users: Users,
    private val tournaments: Tournaments,
    private val playerRosters: PlayerRosters,
) : ResetPlayerRoster, AddPlayer {

    override fun resetPlayerRoster(tournamentId: TournamentId) {
        playerRosters.remove(tournamentId)
    }

    override fun addPlayer(tournamentId: TournamentId, userId: UserId, addedOn: () -> LocalDate): Player {
        val user = users.get(userId)
        val playerRoster = playerRosters.get(tournamentId)
            .rejectIfAlreadyFull()
        val player = user.toPlayerIn(playerRoster)
        playerRoster
            .rejectOnDuplicate(player)
            .add(player)
            .also { playerRosters.save(it) }
        return player
    }

    private fun Users.get(userId: UserId) = find(userId)
        ?: throw DomainExceptions.UserNotFound(userId)

    private fun PlayerRosters.get(tournamentId: TournamentId) = find(tournamentId)
        ?: tournaments.find(tournamentId)?.toPlayerRoster()
        ?: throw DomainExceptions.TournamentNotFound(tournamentId)

    private fun User.toPlayerIn(playerRoster: PlayerRoster) = Player(
        userId = id,
        nickname = playerRoster.availableNicknameClosestTo(preferredNickname),
    )

    private fun PlayerRoster.rejectIfAlreadyFull() = also {
        if (isFull) throw DomainExceptions.FullPlayerRoster(tournamentId)
    }

    private fun PlayerRoster.rejectOnDuplicate(player: Player) = also {
        if (player.userId in this)
            throw DomainExceptions.DuplicatePlayer(tournamentId, player.userId)
    }

}
