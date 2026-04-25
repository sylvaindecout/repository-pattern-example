package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.api.FindPlayer
import fr.sdecout.repository.domain.api.ListPlayers
import fr.sdecout.repository.domain.core.player.PlayerOverview
import fr.sdecout.repository.domain.core.roster.Player
import fr.sdecout.repository.domain.core.roster.PlayerRoster.Companion.toPlayerRoster
import fr.sdecout.repository.domain.core.scoreboard.Score
import fr.sdecout.repository.domain.core.scoreboard.Score.Companion.points
import fr.sdecout.repository.domain.core.scoreboard.ScoreboardEntry
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.User
import fr.sdecout.repository.domain.core.user.UserId
import fr.sdecout.repository.domain.core.player.PlayerOverview.Companion.with
import fr.sdecout.repository.domain.spi.PlayerRosters
import fr.sdecout.repository.domain.spi.ScoreboardEntries
import fr.sdecout.repository.domain.spi.Tournaments
import fr.sdecout.repository.domain.spi.Users
import java.time.LocalDate

class PlayerAccessService(
    private val users: Users,
    private val tournaments: Tournaments,
    private val playerRosters: PlayerRosters,
    private val scoreboardEntries: ScoreboardEntries,
) : ListPlayers, FindPlayer {

    override fun listPlayers(tournamentId: TournamentId, requestedOn: () -> LocalDate): List<PlayerOverview> =
        playerRosters.get(tournamentId)
            .players.withUserInfo(tournamentId, requestedOn)
            .sortedByDescending { it.score }

    override fun findPlayer(tournamentId: TournamentId, userId: UserId, requestedOn: () -> LocalDate): PlayerOverview? =
        playerRosters.get(tournamentId)[userId]?.withUserInfo(tournamentId, requestedOn)

    private fun PlayerRosters.get(tournamentId: TournamentId) = find(tournamentId)
        ?: tournaments.find(tournamentId)?.toPlayerRoster()
        ?: throw DomainExceptions.TournamentNotFound(tournamentId)

    private fun Collection<Player>.withUserInfo(tournamentId: TournamentId, requestedOn: () -> LocalDate): Collection<PlayerOverview> {
        val usersById = users.findIn(map { it.userId }).associateBy { it.id }
        val scores = scoreboardEntries.findAll(tournamentId)
            .associateBy(ScoreboardEntry::userId, ScoreboardEntry::score)
        val now = requestedOn()
        return map { player ->
            val user = usersById[player.userId] ?: throw DomainExceptions.UserNotFound(player.userId)
            val score = scores[player.userId] ?: 0.points
            player.withUserInfo(user, score, now)
        }
    }

    private fun Player.withUserInfo(tournamentId: TournamentId, requestedOn: () -> LocalDate): PlayerOverview {
        val user = users.find(userId) ?: throw DomainExceptions.UserNotFound(userId)
        val score = scoreboardEntries.find(tournamentId, userId)?.score ?: 0.points
        return withUserInfo(user, score, requestedOn())
    }

    private fun Player.withUserInfo(user: User, score: Score, now: LocalDate) =
        with(age = user.age { now }, city = user.city, score = score)
}
