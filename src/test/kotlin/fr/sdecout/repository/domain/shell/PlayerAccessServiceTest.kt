package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.TestData.Players
import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
import fr.sdecout.repository.domain.TestData.today
import fr.sdecout.repository.domain.core.roster.PlayerRosterEntry
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.spi.InMemoryPlayerRosterEntries
import fr.sdecout.repository.domain.spi.InMemoryTournaments
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlayerAccessServiceTest {
    val tournaments = InMemoryTournaments()
    val playerRosterEntries = InMemoryPlayerRosterEntries()

    val service = PlayerAccessService(tournaments, playerRosterEntries)

    @AfterEach
    fun afterEach() {
        tournaments.clear()
        playerRosterEntries.clear()
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
        playerRosterEntries.save(PlayerRosterEntry.from(tournament1.id, Players.jolyne.userId, Players.jolyne.nickname))
        playerRosterEntries.save(PlayerRosterEntry.from(tournament1.id, Players.giorno.userId, Players.giorno.nickname))
        playerRosterEntries.save(PlayerRosterEntry.from(tournament1.id, Players.joseph.userId, Players.joseph.nickname))

        val result = service.listPlayers(tournament1.id, requestedOn = { today })

        result shouldBe listOf(
            Players.giorno,
            Players.jolyne,
            Players.joseph,
        )
    }

}
