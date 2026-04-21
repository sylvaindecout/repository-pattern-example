package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.api.ListPlayers
import fr.sdecout.repository.domain.core.player.PlayerOverview
import fr.sdecout.repository.domain.core.player.PlayerOverview.Companion.with
import fr.sdecout.repository.domain.core.roster.Player
import fr.sdecout.repository.domain.core.roster.PlayerRoster.Companion.toPlayerRoster
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.User
import fr.sdecout.repository.domain.spi.PlayerRosters
import fr.sdecout.repository.domain.spi.Tournaments
import fr.sdecout.repository.domain.spi.Users
import java.time.LocalDate

class PlayerAccessService(
    private val users: Users,
    private val tournaments: Tournaments,
    private val playerRosters: PlayerRosters,
) : ListPlayers {

    override fun listPlayers(tournamentId: TournamentId, requestedOn: () -> LocalDate): List<PlayerOverview> =
        playerRosters.get(tournamentId)
            .players.withUserInfo(requestedOn)
            .sortedBy { it.nickname.value }

    private fun PlayerRosters.get(tournamentId: TournamentId) = find(tournamentId)
        ?: tournaments.find(tournamentId)?.toPlayerRoster()
        ?: throw DomainExceptions.TournamentNotFound(tournamentId)

    private fun Collection<Player>.withUserInfo(requestedOn: () -> LocalDate): Collection<PlayerOverview> {
        val usersById = users.findIn(map { it.userId }).associateBy { it.id }
        val now = requestedOn()
        return map { player ->
            val user = usersById[player.userId] ?: throw DomainExceptions.UserNotFound(player.userId)
            player.withUserInfo(user, now)
        }
    }

    private fun Player.withUserInfo(user: User, now: LocalDate) =
        with(age = user.age { now }, city = user.city)
}
