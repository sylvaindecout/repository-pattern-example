package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.api.UpsertTournament
import fr.sdecout.repository.domain.core.tournament.Tournament
import fr.sdecout.repository.domain.spi.Tournaments

class TournamentUpdateService(private val tournaments: Tournaments) : UpsertTournament {

    override fun upsert(tournament: Tournament) = tournaments.save(tournament)

}
