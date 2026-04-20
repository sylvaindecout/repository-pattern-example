package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.api.FindTournament
import fr.sdecout.repository.domain.core.tournament.Tournament
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.spi.Tournaments

class TournamentAccessService(private val tournaments: Tournaments) : FindTournament {

    override fun findTournament(tournamentId: TournamentId): Tournament? =
        tournaments.find(tournamentId)

}
