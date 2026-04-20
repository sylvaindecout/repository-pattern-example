package fr.sdecout.repository.domain

import fr.sdecout.repository.domain.TestData.Players
import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
import fr.sdecout.repository.domain.TestData.Users.jotaro
import fr.sdecout.repository.domain.TestData.today
import fr.sdecout.repository.domain.api.*
import fr.sdecout.repository.domain.shell.*
import fr.sdecout.repository.domain.spi.InMemoryPlayerRosterEntries
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
    val playerRosterEntries = InMemoryPlayerRosterEntries()

    // driving ports
    val upsertUser: UpsertUser = UserUpdateService(users)
    val findUser: FindUser = UserAccessService(users)
    val upsertTournament: UpsertTournament = TournamentUpdateService(tournaments)
    val findTournament: FindTournament = TournamentAccessService(tournaments)
    val addPlayer: AddPlayer = PlayerUpdateService(users, tournaments, playerRosterEntries)
    val listPlayers: ListPlayers = PlayerAccessService(tournaments, playerRosterEntries)
    val resetPlayerRoster: ResetPlayerRoster = PlayerUpdateService(users, tournaments, playerRosterEntries)

    @Test
    fun `should support user journey`() {
        // back office - configure user
        upsertUser(jotaro)
        findUser(jotaro.id) shouldBe jotaro

        // back office - configure tournament
        upsertTournament(tournament1)
        findTournament(tournament1.id) shouldBe tournament1

        // configure player info
        val player = Players.jotaro

        // select a tournament
        val selectedTournamentId = tournament1.id

        // complete 1st challenge
        addPlayer(selectedTournamentId, jotaro.id, addedOn = { today })

        // complete session
        listPlayers(selectedTournamentId, requestedOn = { today }) shouldBe listOf(player)
        resetPlayerRoster(selectedTournamentId)
        listPlayers(selectedTournamentId, requestedOn = { today }) shouldBe emptyList()
    }
}
