package fr.sdecout.repository.domain.spi

import fr.sdecout.repository.domain.core.roster.PlayerRosterEntry
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.UserId

interface PlayerRosterEntries {
    fun find(tournamentId: TournamentId, userId: UserId): PlayerRosterEntry?
    fun findAll(tournamentId: TournamentId): List<PlayerRosterEntry>
    fun save(playerRosterEntry: PlayerRosterEntry)
    fun removeAll(tournamentId: TournamentId)
}
