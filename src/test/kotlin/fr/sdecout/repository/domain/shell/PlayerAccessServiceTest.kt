package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.TestData.Players
import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
import fr.sdecout.repository.domain.TestData.today
import fr.sdecout.repository.domain.core.roster.PlayerRoster.Companion.toPlayerRoster
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.spi.PlayerRosters
import fr.sdecout.repository.domain.spi.Tournaments
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlayerAccessServiceTest {
    val tournaments = mockk<Tournaments>(relaxed = true)
    val playerRosters = mockk<PlayerRosters>(relaxed = true)

    val service = PlayerAccessService(tournaments, playerRosters)

    @BeforeEach
    fun beforeEach() {
        every { tournaments.find(tournament1.id) } returns tournament1
    }

    @Test
    fun `should fail to list players for an unknown tournament`() {
        val unknownTournamentId = TournamentId.from("unknown-tournament")
        every { tournaments.find(unknownTournamentId) } returns null
        every { playerRosters.find(unknownTournamentId) } returns null

        shouldThrow<DomainExceptions.TournamentNotFound> {
            service.listPlayers(unknownTournamentId, requestedOn = { today })
        }.message shouldBe "No tournament found with id $unknownTournamentId"
    }

    /**
     * Issue: Consistency - the result for `find(tournament1.id)` could have `tournamentId = tournament2.id`
     */
    @Test
    fun `should list players`() {
        every { playerRosters.find(tournament1.id) } returns tournament1.toPlayerRoster(Players.jolyne, Players.giorno, Players.joseph)

        val result = service.listPlayers(tournament1.id, requestedOn = { today })

        result shouldBe listOf(
            Players.giorno,
            Players.jolyne,
            Players.joseph,
        )
    }

}
