package fr.sdecout.repository.infra.driven.jdbc

import fr.sdecout.repository.domain.core.search.TournamentSearchCriteria
import fr.sdecout.repository.domain.core.search.TournamentSearchCriterion.AgeRestriction
import fr.sdecout.repository.domain.core.search.TournamentSearchResultItem
import fr.sdecout.repository.domain.core.search.meets
import fr.sdecout.repository.domain.core.tournament.RosterSize
import fr.sdecout.repository.domain.core.tournament.RosterSize.Companion.players
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.tournament.TournamentName
import fr.sdecout.repository.domain.core.user.Age
import fr.sdecout.repository.domain.core.user.Age.Companion.yearsOld
import fr.sdecout.repository.domain.spi.TournamentSearchResultItems
import fr.sdecout.repository.infra.driven.jdbc.functions.PostgresqlFunctions
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.Tournament
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.references.PLAYER
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.references.ROSTER_ENTRY
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.references.TOURNAMENT
import org.jooq.*
import org.jooq.impl.DSL
import java.time.LocalDate

class DbTournamentSearchResultItems(private val dsl: DSLContext) : TournamentSearchResultItems {
    private typealias Row = Record6<TournamentId?, TournamentName?, RosterSize?, Age?, RosterSize?, Age?>

    @Suppress("PrivatePropertyName", "UnusedReceiverParameter")
    private val Tournament.AVERAGE_AGE: Field<Age> get() =
        DSL.field(DSL.name("averageAge"), Age::class.java)

    @Suppress("PrivatePropertyName", "UnusedReceiverParameter")
    private val Tournament.ROSTER_SIZE: Field<RosterSize> get() =
        DSL.count(ROSTER_ENTRY.PLAYER).convertFrom { it?.players ?: 0.players }

    private fun averageAge(requestedOn: () -> LocalDate): Field<Age> = DSL.extract(
        DSL.avg(PostgresqlFunctions.age(DSL.value(requestedOn()), PLAYER.DATE_OF_BIRTH)),
        DatePart.YEAR,
    ).convertFrom { it?.yearsOld }

    override fun findAll(
        criteria: TournamentSearchCriteria,
        requestedOn: () -> LocalDate,
    ): List<TournamentSearchResultItem> = dsl
        .select(
            TOURNAMENT.ID,
            TOURNAMENT.NAME,
            TOURNAMENT.MAX_PLAYER_ROSTER_SIZE,
            TOURNAMENT.MIN_AGE,
            TOURNAMENT.ROSTER_SIZE,
            averageAge(requestedOn).`as`(TOURNAMENT.AVERAGE_AGE),
        )
        .from(TOURNAMENT)
        .leftJoin(ROSTER_ENTRY).on(ROSTER_ENTRY.TOURNAMENT.eq(TOURNAMENT.ID))
        .leftJoin(PLAYER).on(PLAYER.ID.eq(ROSTER_ENTRY.PLAYER))
        .meeting(criteria)
        .groupBy(TOURNAMENT.ID)
        .fetch { it.toDomain() }
        .filter { it meets criteria }

    private fun SelectOnConditionStep<Row>.meeting(criteria: TournamentSearchCriteria) = criteria.criteria
        .filterIsInstance<AgeRestriction>()
        .firstOrNull().let { ageCriterion ->
            if (ageCriterion == null) this
            else where(TOURNAMENT.MIN_AGE.isNull())
                .or(TOURNAMENT.MIN_AGE.lessOrEqual(ageCriterion.accessibleForAge))
        }

    private fun Row.toDomain(): TournamentSearchResultItem =
        TournamentSearchResultItem(
            id = get(TOURNAMENT.ID)!!,
            name = get(TOURNAMENT.NAME)!!,
            maxPlayerRosterSize = get(TOURNAMENT.MAX_PLAYER_ROSTER_SIZE)!!,
            playerRostersSize = get(TOURNAMENT.ROSTER_SIZE),
            minimumAge = get(TOURNAMENT.MIN_AGE),
            averageAge = get(TOURNAMENT.AVERAGE_AGE),
        )
}
