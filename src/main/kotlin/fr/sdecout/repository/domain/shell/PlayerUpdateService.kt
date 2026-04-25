package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.api.AddPlayer
import fr.sdecout.repository.domain.api.ResetPlayerRoster
import fr.sdecout.repository.domain.api.UpdateScore
import fr.sdecout.repository.domain.core.alerting.Notification
import fr.sdecout.repository.domain.core.alerting.PriorityLevel.HIGH
import fr.sdecout.repository.domain.core.player.PlayerOverview
import fr.sdecout.repository.domain.core.roster.Player
import fr.sdecout.repository.domain.core.roster.PlayerRoster
import fr.sdecout.repository.domain.core.roster.PlayerRoster.Companion.toPlayerRoster
import fr.sdecout.repository.domain.core.roster.availableNicknameClosestTo
import fr.sdecout.repository.domain.core.scoreboard.Score
import fr.sdecout.repository.domain.core.scoreboard.Score.Companion.points
import fr.sdecout.repository.domain.core.scoreboard.ScoreboardEntry
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.User
import fr.sdecout.repository.domain.core.user.UserId
import fr.sdecout.repository.domain.spi.*
import java.time.LocalDate

class PlayerUpdateService(
    private val users: Users,
    private val tournaments: Tournaments,
    private val playerRosters: PlayerRosters,
    private val scoreboardEntries: ScoreboardEntries,
    private val alerting: Alerting,
) : ResetPlayerRoster, AddPlayer, UpdateScore {

    override fun resetPlayerRoster(tournamentId: TournamentId) {
        playerRosters.remove(tournamentId)
        scoreboardEntries.removeAll(tournamentId)
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
        ScoreboardEntry.new(tournamentId, player.userId)
            .also { scoreboardEntries.save(it) }
        return player
    }

    override fun updateScore(tournamentId: TournamentId, userId: UserId, score: Score) {
        scoreboardEntries.get(tournamentId, userId)
            .update(score)
            .also { scoreboardEntries.save(it) }
    }

    private fun Users.get(userId: UserId) = find(userId)
        ?: throw DomainExceptions.UserNotFound(userId)

    private fun PlayerRosters.get(tournamentId: TournamentId) = find(tournamentId)
        ?: tournaments.find(tournamentId)?.toPlayerRoster()
        ?: throw DomainExceptions.TournamentNotFound(tournamentId)

    private fun ScoreboardEntries.get(tournamentId: TournamentId, userId: UserId) =
        find(tournamentId, userId) ?: ScoreboardEntry.new(tournamentId, userId)

    private fun User.toPlayerIn(playerRoster: PlayerRoster, addedOn: () -> LocalDate) = PlayerOverview(
        userId = id,
        nickname = playerRoster.availableNicknameClosestTo(preferredNickname),
        age = age(addedOn),
        city = city,
        score = 0.points,
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

    private fun sendAlert(content: String) = alerting.send(Notification.of(priority = HIGH, content))

}
