package fr.sdecout.repository.infra.driven.jdbc

import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.records.TournamentRecord
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.references.TOURNAMENT
import org.jooq.DSLContext

class TournamentRepositoryImpl(private val dsl: DSLContext) : TournamentRepository {

    override fun find(id: String): TournamentRecord? = dsl
        .selectFrom(TOURNAMENT)
        .where(TOURNAMENT.ID.equal(TournamentId.from(id)))
        .fetchOne()

    override fun findAll(): List<TournamentRecord> = dsl
        .selectFrom(TOURNAMENT)
        .fetch()

    override fun save(tournament: TournamentRecord) {
        tournament.let { record ->
            dsl.insertInto(TOURNAMENT)
                .set(record)
                .onDuplicateKeyUpdate()
                .set(record)
                .execute()
        }
    }

}
