package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.api.ListPlayers
import fr.sdecout.repository.domain.core.roster.Player
import fr.sdecout.repository.domain.core.roster.PlayerRosterEntry
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.spi.PlayerRosterEntries
import fr.sdecout.repository.domain.spi.Tournaments
import java.time.LocalDate

class PlayerAccessService(
    private val tournaments: Tournaments,
    private val playerRosterEntries: PlayerRosterEntries,
) : ListPlayers {

    override fun listPlayers(tournamentId: TournamentId, requestedOn: () -> LocalDate): List<Player> =
        playerRosterEntries.get(tournamentId)
            .players
            .sortedBy { it.nickname.value }

    private fun PlayerRosterEntries.get(tournamentId: TournamentId) = findAll(tournamentId)
        .also {
            if (it.isEmpty() && tournaments.find(tournamentId) == null)
                throw DomainExceptions.TournamentNotFound(tournamentId)
        }

    private val List<PlayerRosterEntry>.players get() = map { Player(it.userId, it.nickname) }
}
