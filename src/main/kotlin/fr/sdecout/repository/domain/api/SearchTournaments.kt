package fr.sdecout.repository.domain.api

import fr.sdecout.repository.domain.core.search.TournamentSearchCriteria
import fr.sdecout.repository.domain.core.search.TournamentSearchResult
import java.time.LocalDate

fun interface SearchTournaments {
    fun searchTournaments(criteria: TournamentSearchCriteria, requestedOn: () -> LocalDate): TournamentSearchResult

    operator fun invoke(criteria: TournamentSearchCriteria, requestedOn: () -> LocalDate) =
        searchTournaments(criteria, requestedOn)
}

