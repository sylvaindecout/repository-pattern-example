package fr.sdecout.repository.domain.spi

import fr.sdecout.repository.domain.core.tournament.Tournament
import fr.sdecout.repository.domain.core.tournament.TournamentId

class InMemoryTournaments : Tournaments {
    private val values = mutableMapOf<TournamentId, Tournament>()

    override fun find(id: TournamentId): Tournament? = values[id]

    override fun findAll(): List<Tournament> = values.values.toList()

    override fun save(tournament: Tournament) {
        values[tournament.id] = tournament
    }

    fun clear() = values.clear()
}
