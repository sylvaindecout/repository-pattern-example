package fr.sdecout.repository.domain.core.search

import fr.sdecout.repository.domain.core.user.Age

class TournamentSearchCriteria private constructor(
    val criteria: Collection<TournamentSearchCriterion>,
) {
    companion object {
        fun all() = TournamentSearchCriteria(emptyList())
        fun where(criterion: TournamentSearchCriterion) = TournamentSearchCriteria(listOf(criterion))
        infix fun TournamentSearchCriteria.and(criterion: TournamentSearchCriterion) =
            TournamentSearchCriteria(this.criteria + criterion)
    }

    override fun toString() = criteria.joinToString { it.toString() }
}

infix fun TournamentSearchResultItem.meets(criteria: TournamentSearchCriteria): Boolean =
    criteria.criteria.fold(true) { acc, criterion -> acc && criterion isMetBy this }

sealed class TournamentSearchCriterion(
    private val predicate: (TournamentSearchResultItem) -> Boolean,
) {
    companion object {
        fun accessibleForAge(age: Age): TournamentSearchCriterion = AgeRestriction(age)
        fun openSlotsOnly(): TournamentSearchCriterion = OpenSlotsOnly
    }

    infix fun isMetBy(item: TournamentSearchResultItem): Boolean = predicate(item)

    data class AgeRestriction(val accessibleForAge: Age) : TournamentSearchCriterion(
        predicate = { item -> item.minimumAge == null || item.minimumAge <= accessibleForAge },
    )

    object OpenSlotsOnly : TournamentSearchCriterion(predicate = { item -> !item.isFull })
}
