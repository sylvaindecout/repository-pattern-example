package fr.sdecout.repository.domain

import fr.sdecout.repository.domain.TestData.Players
import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
import fr.sdecout.repository.domain.TestData.Users.jotaro
import fr.sdecout.repository.domain.TestData.today
import fr.sdecout.repository.domain.api.*
import fr.sdecout.repository.domain.core.roster.PlayerRoster.Companion.toPlayerRoster
import fr.sdecout.repository.domain.shell.*
import fr.sdecout.repository.domain.spi.PlayerRosters
import fr.sdecout.repository.domain.spi.Tournaments
import fr.sdecout.repository.domain.spi.Users
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("Scaffolding")
class UserJourneyTest {
    // driven ports
    val users = mockk<Users>(relaxed = true)
    val tournaments = mockk<Tournaments>(relaxed = true)
    val playerRosters = mockk<PlayerRosters>(relaxed = true)

    // driving ports
    val upsertUser: UpsertUser = UserUpdateService(users)
    val findUser: FindUser = UserAccessService(users)
    val upsertTournament: UpsertTournament = TournamentUpdateService(tournaments)
    val findTournament: FindTournament = TournamentAccessService(tournaments)
    val addPlayer: AddPlayer = PlayerUpdateService(users, tournaments, playerRosters)
    val listPlayers: ListPlayers = PlayerAccessService(tournaments, playerRosters)
    val resetPlayerRoster: ResetPlayerRoster = PlayerUpdateService(users, tournaments, playerRosters)

    /**
     * Issue: Verbosity and consistency - we need to update the mocks on each step.
     * This becomes very complex when you test a method that calls a mocked method more than once.
     */
    @Test
    fun `should support user journey`() {
        every { playerRosters.find(any()) } returns null

        // back office - configure user
        upsertUser(jotaro)
        verify { users.save(jotaro) }
        every { users.find(jotaro.id) } returns jotaro
        findUser(jotaro.id) shouldBe jotaro

        // back office - configure tournament
        upsertTournament(tournament1)
        verify { tournaments.save(tournament1) }
        every { tournaments.find(tournament1.id) } returns tournament1
        every { tournaments.findAll() } returns listOf(tournament1)
        findTournament(tournament1.id) shouldBe tournament1

        // configure player info
        val player = Players.jotaro

        // select a tournament
        val selectedTournamentId = tournament1.id

        // complete 1st challenge
        addPlayer(selectedTournamentId, jotaro.id, addedOn = { today })
        verify { playerRosters.save(tournament1.toPlayerRoster().add(Players.jotaro)) }
        every { playerRosters.find(tournament1.id) } returns tournament1.toPlayerRoster(Players.jotaro)

        // complete session
        listPlayers(selectedTournamentId, requestedOn = { today }) shouldBe listOf(player)
        resetPlayerRoster(selectedTournamentId)
        verify { playerRosters.remove(tournament1.id) }
        every { playerRosters.find(tournament1.id) } returns tournament1.toPlayerRoster()
        listPlayers(selectedTournamentId, requestedOn = { today }) shouldBe emptyList()
    }
}
