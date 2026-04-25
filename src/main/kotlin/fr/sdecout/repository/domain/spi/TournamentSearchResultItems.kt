package fr.sdecout.repository.domain.spi

import fr.sdecout.repository.domain.core.search.TournamentSearchCriteria
import fr.sdecout.repository.domain.core.search.TournamentSearchResultItem
import java.time.LocalDate

interface TournamentSearchResultItems {
    fun findAll(criteria: TournamentSearchCriteria, requestedOn: () -> LocalDate): List<TournamentSearchResultItem>
}
