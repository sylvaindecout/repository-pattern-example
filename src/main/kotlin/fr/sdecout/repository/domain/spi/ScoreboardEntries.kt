package fr.sdecout.repository.domain.spi

import fr.sdecout.repository.domain.core.scoreboard.ScoreboardEntry
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.UserId

interface ScoreboardEntries {
    fun find(tournamentId: TournamentId, userId: UserId): ScoreboardEntry?
    fun findAll(tournamentId: TournamentId): List<ScoreboardEntry>
    fun save(scoreboardEntry: ScoreboardEntry)
    fun removeAll(tournamentId: TournamentId)
}
