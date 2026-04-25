package fr.sdecout.repository.domain.spi

import fr.sdecout.repository.domain.core.roster.PlayerRoster
import fr.sdecout.repository.domain.core.search.TournamentSearchCriteria
import fr.sdecout.repository.domain.core.search.TournamentSearchResultItem
import fr.sdecout.repository.domain.core.search.meets
import fr.sdecout.repository.domain.core.tournament.RosterSize.Companion.players
import fr.sdecout.repository.domain.core.tournament.Tournament
import fr.sdecout.repository.domain.core.user.Age.Companion.average
import java.time.LocalDate

class CompositeTournamentSearchResultItems(
    private val users: Users,
    private val tournaments: Tournaments,
    private val playerRosters: PlayerRosters,
) : TournamentSearchResultItems {

    override fun findAll(criteria: TournamentSearchCriteria, requestedOn: () -> LocalDate): List<TournamentSearchResultItem> =
        tournaments.findAll()
            .map { tournament -> tournament.toSearchResultItem(requestedOn) }
            .filter { it meets criteria }

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
