package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.api.FindTournament
import fr.sdecout.repository.domain.api.SearchTournaments
import fr.sdecout.repository.domain.core.search.TournamentSearchCriteria
import fr.sdecout.repository.domain.core.search.TournamentSearchResult
import fr.sdecout.repository.domain.core.tournament.Tournament
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.spi.TournamentSearchResultItems
import fr.sdecout.repository.domain.spi.Tournaments
import java.time.LocalDate

class TournamentAccessService(
    private val tournaments: Tournaments,
    private val tournamentSearchResultItems: TournamentSearchResultItems,
) : FindTournament, SearchTournaments {

    override fun findTournament(tournamentId: TournamentId): Tournament? =
        tournaments.find(tournamentId)

    override fun searchTournaments(criteria: TournamentSearchCriteria, requestedOn: () -> LocalDate): TournamentSearchResult =
        TournamentSearchResult.of(tournamentSearchResultItems.findAll(criteria, requestedOn))

}
