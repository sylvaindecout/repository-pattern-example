package fr.sdecout.repository.infra.driven.jdbc

import fr.sdecout.repository.domain.core.roster.PlayerRosterEntry
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.UserId
import fr.sdecout.repository.domain.spi.PlayerRosterEntries
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.records.RosterEntryRecord
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.references.ROSTER_ENTRY
import org.jooq.DSLContext

class DbPlayerRosterEntries(private val dsl: DSLContext) : PlayerRosterEntries {

    override fun find(tournamentId: TournamentId, userId: UserId): PlayerRosterEntry? = dsl
        .selectFrom(ROSTER_ENTRY)
        .where(ROSTER_ENTRY.TOURNAMENT.eq(tournamentId))
        .and(ROSTER_ENTRY.PLAYER.eq(userId))
        .fetchOne { it.toDomain() }

    override fun findAll(tournamentId: TournamentId): List<PlayerRosterEntry> = dsl
        .selectFrom(ROSTER_ENTRY)
        .where(ROSTER_ENTRY.TOURNAMENT.eq(tournamentId))
        .fetch { it.toDomain() }

    override fun save(playerRosterEntry: PlayerRosterEntry) {
        playerRosterEntry.toRecord().let { record ->
            dsl.insertInto(ROSTER_ENTRY)
                .set(record)
                .onDuplicateKeyUpdate()
                .set(record)
                .execute()
        }
    }

    override fun removeAll(tournamentId: TournamentId) {
        dsl.deleteFrom(ROSTER_ENTRY)
            .where(ROSTER_ENTRY.TOURNAMENT.eq(tournamentId))
            .execute()
    }

    private fun RosterEntryRecord.toDomain() = PlayerRosterEntry.from(
        tournamentId = tournament,
        userId = player,
        nickname = nickname,
    )

    private fun PlayerRosterEntry.toRecord() = ROSTER_ENTRY.newRecord()
        .with(ROSTER_ENTRY.TOURNAMENT, tournamentId)
        .with(ROSTER_ENTRY.PLAYER, userId)
        .with(ROSTER_ENTRY.NICKNAME, nickname)
}
