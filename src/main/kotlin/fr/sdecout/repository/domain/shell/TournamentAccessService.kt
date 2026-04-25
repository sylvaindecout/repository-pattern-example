package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.api.FindTournament
import fr.sdecout.repository.domain.api.SearchTournaments
import fr.sdecout.repository.domain.core.roster.PlayerRoster
import fr.sdecout.repository.domain.core.search.TournamentSearchCriteria
import fr.sdecout.repository.domain.core.search.TournamentSearchResult
import fr.sdecout.repository.domain.core.search.TournamentSearchResultItem
import fr.sdecout.repository.domain.core.search.meets
import fr.sdecout.repository.domain.core.tournament.RosterSize.Companion.players
import fr.sdecout.repository.domain.core.tournament.Tournament
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.Age.Companion.average
import fr.sdecout.repository.domain.spi.PlayerRosters
import fr.sdecout.repository.domain.spi.Tournaments
import fr.sdecout.repository.domain.spi.Users
import java.time.LocalDate

class TournamentAccessService(
    private val users: Users,
    private val tournaments: Tournaments,
    private val playerRosters: PlayerRosters,
) : FindTournament, SearchTournaments {

    override fun findTournament(tournamentId: TournamentId): Tournament? =
        tournaments.find(tournamentId)

    /**
     * Issue: All tournaments need to be loaded before criteria can be applied.
     * Depending on the number of tournaments, this may be a problem.
     */
    override fun searchTournaments(criteria: TournamentSearchCriteria, requestedOn: () -> LocalDate): TournamentSearchResult =
        TournamentSearchResult.of(tournaments.findAll()
            .map { tournament -> tournament.toSearchResultItem(requestedOn) }
            .filter { it meets criteria })

    private fun Tournament.toSearchResultItem(requestedOn: () -> LocalDate) = playerRosters.find(id)
        .let { playerRoster ->
            TournamentSearchResultItem(
                id = id,
                name = name,
                maxPlayerRosterSize = maxPlayerRosterSize,
                playerRostersSize = playerRoster?.size ?: 0.players,
                minimumAge = minimumAge,
                averageAge = playerRoster?.averageAge(requestedOn),
            )
        }

    private fun PlayerRoster.averageAge(computedOn: () -> LocalDate) = players
        .let { players -> users.findIn(players.map { player -> player.userId }) }
        .map { user -> user.age(computedOn) }
        .average()
}
