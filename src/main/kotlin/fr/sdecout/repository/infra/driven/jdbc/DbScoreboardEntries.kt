package fr.sdecout.repository.infra.driven.jdbc

import fr.sdecout.repository.domain.core.scoreboard.ScoreboardEntry
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.UserId
import fr.sdecout.repository.domain.spi.ScoreboardEntries
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.records.ScoreboardEntryRecord
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.references.SCOREBOARD_ENTRY
import org.jooq.DSLContext

class DbScoreboardEntries(private val dsl: DSLContext) : ScoreboardEntries {

    override fun find(tournamentId: TournamentId, userId: UserId): ScoreboardEntry? = dsl
        .selectFrom(SCOREBOARD_ENTRY)
        .where(SCOREBOARD_ENTRY.TOURNAMENT.eq(tournamentId))
        .and(SCOREBOARD_ENTRY.PLAYER.eq(userId))
        .fetchOne { it.toDomain() }

    override fun findAll(tournamentId: TournamentId): List<ScoreboardEntry> = dsl
        .selectFrom(SCOREBOARD_ENTRY)
        .where(SCOREBOARD_ENTRY.TOURNAMENT.eq(tournamentId))
        .fetch { it.toDomain() }

    override fun save(scoreboardEntry: ScoreboardEntry) {
        scoreboardEntry.toRecord().let { record ->
            dsl.insertInto(SCOREBOARD_ENTRY)
                .set(record)
                .onDuplicateKeyUpdate()
                .set(record)
                .execute()
        }
    }

    override fun removeAll(tournamentId: TournamentId) {
        dsl.deleteFrom(SCOREBOARD_ENTRY)
            .where(SCOREBOARD_ENTRY.TOURNAMENT.eq(tournamentId))
            .execute()
    }

    private fun ScoreboardEntryRecord.toDomain() = ScoreboardEntry.from(
        tournamentId = tournament,
        userId = player,
        score = score,
    )

    private fun ScoreboardEntry.toRecord() = SCOREBOARD_ENTRY.newRecord()
        .with(SCOREBOARD_ENTRY.TOURNAMENT, tournamentId)
        .with(SCOREBOARD_ENTRY.PLAYER, userId)
        .with(SCOREBOARD_ENTRY.SCORE, score)
}
