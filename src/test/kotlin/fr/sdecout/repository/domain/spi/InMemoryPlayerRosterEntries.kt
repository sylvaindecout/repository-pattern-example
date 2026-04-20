package fr.sdecout.repository.domain.spi

import fr.sdecout.repository.domain.core.roster.PlayerRosterEntry
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.UserId

class InMemoryPlayerRosterEntries : PlayerRosterEntries {
    private val values = mutableMapOf<TournamentId, MutableMap<UserId, PlayerRosterEntry>>()

    override fun find(tournamentId: TournamentId, userId: UserId): PlayerRosterEntry? = values[tournamentId]?.get(userId)

    override fun findAll(tournamentId: TournamentId): List<PlayerRosterEntry> =
        values[tournamentId]?.values?.toList() ?: emptyList()

    override fun save(playerRosterEntry: PlayerRosterEntry) {
        val tournament = values.getOrPut(playerRosterEntry.tournamentId) { mutableMapOf() }
        tournament[playerRosterEntry.userId] = playerRosterEntry
    }

    override fun removeAll(tournamentId: TournamentId) {
        values[tournamentId]?.clear()
    }

    fun clear() = values.clear()
}
