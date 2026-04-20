package fr.sdecout.repository.domain

import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
import fr.sdecout.repository.domain.TestData.Users.jotaro
import fr.sdecout.repository.domain.api.FindTournament
import fr.sdecout.repository.domain.api.FindUser
import fr.sdecout.repository.domain.api.UpsertTournament
import fr.sdecout.repository.domain.api.UpsertUser
import fr.sdecout.repository.domain.shell.TournamentAccessService
import fr.sdecout.repository.domain.shell.TournamentUpdateService
import fr.sdecout.repository.domain.shell.UserAccessService
import fr.sdecout.repository.domain.shell.UserUpdateService
import fr.sdecout.repository.domain.spi.InMemoryTournaments
import fr.sdecout.repository.domain.spi.InMemoryUsers
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("Scaffolding")
class UserJourneyTest {
    // driven ports
    val users = InMemoryUsers()
    val tournaments = InMemoryTournaments()

    // driving ports
    val upsertUser: UpsertUser = UserUpdateService(users)
    val findUser: FindUser = UserAccessService(users)
    val upsertTournament: UpsertTournament = TournamentUpdateService(tournaments)
    val findTournament: FindTournament = TournamentAccessService(tournaments)

    @Test
    fun `should support user journey`() {
        // back office - configure user
        upsertUser(jotaro)
        findUser(jotaro.id) shouldBe jotaro

        // back office - configure tournament
        upsertTournament(tournament1)
        findTournament(tournament1.id) shouldBe tournament1
    }
}
