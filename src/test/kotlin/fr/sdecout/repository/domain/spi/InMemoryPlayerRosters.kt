package fr.sdecout.repository.domain.spi

import fr.sdecout.repository.domain.core.roster.PlayerRoster
import fr.sdecout.repository.domain.core.tournament.TournamentId

class InMemoryPlayerRosters : PlayerRosters {
    private val values = mutableMapOf<TournamentId, PlayerRoster>()

    override fun find(id: TournamentId): PlayerRoster? = values[id]

    override fun save(playerRoster: PlayerRoster) {
        values[playerRoster.tournamentId] = playerRoster
    }

    override fun remove(id: TournamentId) {
        values.remove(id)
    }

    fun clear() = values.clear()
}
