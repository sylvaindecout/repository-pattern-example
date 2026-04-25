package fr.sdecout.repository.domain.spi

import fr.sdecout.repository.domain.core.scoreboard.ScoreboardEntry
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.UserId

class InMemoryScoreboardEntries : ScoreboardEntries {
    private val values = mutableMapOf<TournamentId, MutableMap<UserId, ScoreboardEntry>>()

    override fun find(tournamentId: TournamentId, userId: UserId): ScoreboardEntry? = values[tournamentId]?.get(userId)

    override fun findAll(tournamentId: TournamentId): List<ScoreboardEntry> =
        values[tournamentId]?.values?.toList() ?: emptyList()

    override fun save(scoreboardEntry: ScoreboardEntry) {
        val tournament = values.getOrPut(scoreboardEntry.tournamentId) { mutableMapOf() }
        tournament[scoreboardEntry.userId] = scoreboardEntry
    }

    override fun removeAll(tournamentId: TournamentId) {
        values[tournamentId]?.clear()
    }

    fun clear() = values.clear()
}
