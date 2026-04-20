package fr.sdecout.repository.domain.spi

import fr.sdecout.repository.domain.core.roster.PlayerRoster
import fr.sdecout.repository.domain.core.tournament.TournamentId

interface PlayerRosters {
    fun find(id: TournamentId): PlayerRoster?
    fun save(playerRoster: PlayerRoster)
    fun remove(id: TournamentId)
}
