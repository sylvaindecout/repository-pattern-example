package fr.sdecout.repository.infra.driven.jdbc

import fr.sdecout.repository.domain.core.tournament.Tournament
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.spi.Tournaments
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.records.TournamentRecord
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.references.TOURNAMENT

class DbTournaments(private val repository: TournamentRepository) : Tournaments {

    override fun find(id: TournamentId): Tournament? =
        repository.find(id.value)?.toDomain()

    override fun findAll(): List<Tournament> =
        repository.findAll().map { it.toDomain() }

    override fun save(tournament: Tournament) {
        repository.save(tournament.toRecord())
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
