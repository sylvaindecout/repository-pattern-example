package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.TestData.Players
import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
import fr.sdecout.repository.domain.TestData.today
import fr.sdecout.repository.domain.core.roster.PlayerRoster.Companion.toPlayerRoster
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.spi.InMemoryPlayerRosters
import fr.sdecout.repository.domain.spi.InMemoryTournaments
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlayerAccessServiceTest {
    val tournaments = InMemoryTournaments()
    val playerRosters = InMemoryPlayerRosters()

    val service = PlayerAccessService(tournaments, playerRosters)

    @AfterEach
    fun afterEach() {
        tournaments.clear()
        playerRosters.clear()
    }

    @BeforeEach
    fun beforeEach() {
        tournaments.save(tournament1)
    }

    @Test
    fun `should fail to list players for an unknown tournament`() {
        val unknownTournamentId = TournamentId.from("unknown-tournament")
        shouldThrow<DomainExceptions.TournamentNotFound> {
            service.listPlayers(unknownTournamentId, requestedOn = { today })
        }.message shouldBe "No tournament found with id $unknownTournamentId"
    }

    @Test
    fun `should list players`() {
        playerRosters.save(tournament1.toPlayerRoster(Players.jolyne, Players.giorno, Players.joseph))

        val result = service.listPlayers(tournament1.id, requestedOn = { today })

        result shouldBe listOf(
            Players.giorno,
            Players.jolyne,
            Players.joseph,
        )
    }

}
