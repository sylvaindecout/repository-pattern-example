package fr.sdecout.repository.domain.spi

import fr.sdecout.repository.domain.core.tournament.Tournament
import fr.sdecout.repository.domain.core.tournament.TournamentId

interface Tournaments {
    fun find(id: TournamentId): Tournament?
    fun findAll(): List<Tournament>
    fun save(tournament: Tournament)
}
