package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.UserId

object DomainExceptions {

    class TournamentNotFound(tournamentId: TournamentId) :
        RuntimeException("No tournament found with id $tournamentId")

    class UserNotFound(userId: UserId) : RuntimeException("No user found with id $userId")

    class FullPlayerRoster(tournamentId: TournamentId) :
        RuntimeException("Player roster is full for tournament with id $tournamentId")

    class DuplicatePlayer(tournamentId: TournamentId, userId: UserId) :
        RuntimeException("Tournament with id $tournamentId already includes a player with id $userId")

}
