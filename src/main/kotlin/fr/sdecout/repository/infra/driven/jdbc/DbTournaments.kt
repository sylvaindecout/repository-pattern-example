package fr.sdecout.repository.infra.driven.jdbc

import fr.sdecout.repository.domain.core.tournament.Tournament
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.spi.Tournaments
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.records.TournamentRecord
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.references.TOURNAMENT
import org.jooq.DSLContext

class DbTournaments(private val dsl: DSLContext) : Tournaments {

    override fun find(id: TournamentId): Tournament? = dsl
        .selectFrom(TOURNAMENT)
        .where(TOURNAMENT.ID.equal(id))
        .fetchOne { it.toDomain() }

    override fun findAll(): List<Tournament> = dsl
        .selectFrom(TOURNAMENT)
        .fetch { row -> row.toDomain() }

    override fun save(tournament: Tournament) {
        tournament.toRecord().let { record ->
            dsl.insertInto(TOURNAMENT)
                .set(record)
                .onDuplicateKeyUpdate()
                .set(record)
                .execute()
        }
    }

    private fun TournamentRecord.toDomain() = Tournament(
        id = id,
        name = name,
        maxPlayerRosterSize = maxPlayerRosterSize,
        minimumAge = minAge,
    )

    private fun Tournament.toRecord() = TOURNAMENT.newRecord()
        .with(TOURNAMENT.ID, id)
        .with(TOURNAMENT.NAME, name)
        .with(TOURNAMENT.MAX_PLAYER_ROSTER_SIZE, maxPlayerRosterSize)
        .with(TOURNAMENT.MIN_AGE, minimumAge)
}
