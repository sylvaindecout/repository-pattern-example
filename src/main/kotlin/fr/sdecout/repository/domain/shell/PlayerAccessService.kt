package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.api.ListPlayers
import fr.sdecout.repository.domain.core.roster.Player
import fr.sdecout.repository.domain.core.roster.PlayerRoster.Companion.toPlayerRoster
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.spi.PlayerRosters
import fr.sdecout.repository.domain.spi.Tournaments
import java.time.LocalDate

class PlayerAccessService(
    private val tournaments: Tournaments,
    private val playerRosters: PlayerRosters,
) : ListPlayers {

    override fun listPlayers(tournamentId: TournamentId, requestedOn: () -> LocalDate): List<Player> =
        playerRosters.get(tournamentId)
            .players
            .sortedBy { it.nickname.value }

    private fun PlayerRosters.get(tournamentId: TournamentId) = find(tournamentId)
        ?: tournaments.find(tournamentId)?.toPlayerRoster()
        ?: throw DomainExceptions.TournamentNotFound(tournamentId)

}
