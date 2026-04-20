package fr.sdecout.repository.infra.driven.jdbc

import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.records.TournamentRecord

/**
 * Spring-Data-like repository, which manipulates DTOs.
 *
 * Issue: Since it manipulates DTOs, it should not be possible to call it directly from the domain; an adapter is necessary.
 */
interface TournamentRepository {
    fun find(id: String): TournamentRecord?
    fun findAll(): List<TournamentRecord>
    fun save(tournament: TournamentRecord)
}
