package fr.sdecout.repository.infra.driving.rest.selection

import fr.sdecout.repository.domain.core.search.TournamentSearchCriteria
import fr.sdecout.repository.domain.core.search.TournamentSearchCriteria.Companion.and
import fr.sdecout.repository.domain.core.search.TournamentSearchCriteria.Companion.where
import fr.sdecout.repository.domain.core.search.TournamentSearchCriterion.Companion.accessibleForAge
import fr.sdecout.repository.domain.core.search.TournamentSearchCriterion.Companion.openSlotsOnly
import fr.sdecout.repository.domain.core.user.Age.Companion.yearsOld
import jakarta.validation.constraints.PositiveOrZero

data class TournamentSearchRequestDto(
    @field:PositiveOrZero val age: Int?,
) {
    fun toDomain(): TournamentSearchCriteria = listOfNotNull(
        age?.let { accessibleForAge(it.yearsOld) }
    ).fold(where(openSlotsOnly())) { criteria, criterion -> criteria.and(criterion) }
}
