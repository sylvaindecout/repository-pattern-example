package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.api.AddPlayer
import fr.sdecout.repository.domain.api.ResetPlayerRoster
import fr.sdecout.repository.domain.core.alerting.Notification
import fr.sdecout.repository.domain.core.alerting.PriorityLevel.HIGH
import fr.sdecout.repository.domain.core.player.PlayerOverview
import fr.sdecout.repository.domain.core.roster.Player
import fr.sdecout.repository.domain.core.roster.PlayerRoster
import fr.sdecout.repository.domain.core.roster.PlayerRoster.Companion.toPlayerRoster
import fr.sdecout.repository.domain.core.roster.availableNicknameClosestTo
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.User
import fr.sdecout.repository.domain.core.user.UserId
import fr.sdecout.repository.domain.spi.Notifications
import fr.sdecout.repository.domain.spi.PlayerRosters
import fr.sdecout.repository.domain.spi.Tournaments
import fr.sdecout.repository.domain.spi.Users
import java.time.LocalDate

class PlayerUpdateService(
    private val users: Users,
    private val tournaments: Tournaments,
    private val playerRosters: PlayerRosters,
    private val notifications: Notifications,
) : ResetPlayerRoster, AddPlayer {

    override fun resetPlayerRoster(tournamentId: TournamentId) {
        playerRosters.remove(tournamentId)
    }

    override fun addPlayer(tournamentId: TournamentId, userId: UserId, addedOn: () -> LocalDate): PlayerOverview {
        val user = users.get(userId)
        val playerRoster = playerRosters.get(tournamentId)
            .rejectIfAlreadyFull()
        val player = user.toPlayerIn(playerRoster, addedOn)
        playerRoster
            .rejectOnDuplicate(player)
            .rejectOnBrokenAgeLimit(player)
            .add(player)
            .also { playerRosters.save(it) }
        return player
    }

    private fun Users.get(userId: UserId) = find(userId)
        ?: throw DomainExceptions.UserNotFound(userId)

    private fun PlayerRosters.get(tournamentId: TournamentId) = find(tournamentId)
        ?: tournaments.find(tournamentId)?.toPlayerRoster()
        ?: throw DomainExceptions.TournamentNotFound(tournamentId)

    private fun User.toPlayerIn(playerRoster: PlayerRoster, addedOn: () -> LocalDate) = PlayerOverview(
        userId = id,
        nickname = playerRoster.availableNicknameClosestTo(preferredNickname),
        age = age(addedOn),
        city = city,
    )

    private fun PlayerRoster.rejectIfAlreadyFull() = also {
        if (isFull) throw DomainExceptions.FullPlayerRoster(tournamentId)
    }

    private fun PlayerRoster.rejectOnDuplicate(player: PlayerOverview) = also {
        if (player.userId in this)
            throw DomainExceptions.DuplicatePlayer(tournamentId, player.userId)
    }

    private fun PlayerRoster.rejectOnBrokenAgeLimit(player: PlayerOverview) = also {
        if (minimumAge != null && player.age < minimumAge) {
            sendAlert("Player ${player.nickname} (${player.age}) tried to register to tournament restricted to $minimumAge+")
            throw DomainExceptions.BreakingAgeLimit(tournamentId, player.nickname, player.age, minimumAge)
        }
    }

    private fun PlayerRoster.add(player: PlayerOverview) = add(Player(player.userId, player.nickname))

    /**
     * Issue: Does the pattern improve readability? If not, for what benefit?
     */
    private fun sendAlert(content: String) = notifications.add(Notification.of(priority = HIGH, content))

}
