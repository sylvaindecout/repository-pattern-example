package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.TestData.PlayerOverviews
import fr.sdecout.repository.domain.TestData.Players
import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
import fr.sdecout.repository.domain.TestData.Users.giorno
import fr.sdecout.repository.domain.TestData.Users.jolyne
import fr.sdecout.repository.domain.TestData.Users.joseph
import fr.sdecout.repository.domain.TestData.today
import fr.sdecout.repository.domain.core.roster.PlayerRoster.Companion.toPlayerRoster
import fr.sdecout.repository.domain.core.scoreboard.Score.Companion.points
import fr.sdecout.repository.domain.core.scoreboard.ScoreboardEntry
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.spi.InMemoryPlayerRosters
import fr.sdecout.repository.domain.spi.InMemoryScoreboardEntries
import fr.sdecout.repository.domain.spi.InMemoryTournaments
import fr.sdecout.repository.domain.spi.InMemoryUsers
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlayerAccessServiceTest {
    val users = InMemoryUsers()
    val tournaments = InMemoryTournaments()
    val playerRosters = InMemoryPlayerRosters()
    val scoreboardEntries = InMemoryScoreboardEntries()

    val service = PlayerAccessService(users, tournaments, playerRosters, scoreboardEntries)

    @AfterEach
    fun afterEach() {
        users.clear()
        tournaments.clear()
        playerRosters.clear()
        scoreboardEntries.clear()
    }

    @BeforeEach
    fun beforeEach() {
        tournaments.save(tournament1)
        users.save(jolyne)
        users.save(giorno)
        users.save(joseph)
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
        scoreboardEntries.save(ScoreboardEntry.new(tournament1.id, jolyne.id))
        scoreboardEntries.save(ScoreboardEntry.new(tournament1.id, giorno.id).update(12.points))
        scoreboardEntries.save(ScoreboardEntry.new(tournament1.id, joseph.id).update(12.points))

        val result = service.listPlayers(tournament1.id, requestedOn = { today })

        result shouldBe listOf(
            PlayerOverviews.giorno.copy(score = 12.points),
            PlayerOverviews.joseph.copy(score = 12.points),
            PlayerOverviews.jolyne.copy(score = 0.points),
        )
    }

    @Test
    fun `should fail to find player for an unknown tournament`() {
        val unknownTournamentId = TournamentId.from("unknown-tournament")
        shouldThrow<DomainExceptions.TournamentNotFound> {
            service.findPlayer(unknownTournamentId, giorno.id, requestedOn = { today })
        }.message shouldBe "No tournament found with id $unknownTournamentId"
    }

    @Test
    fun `should not find player with unknown user ID`() {
        playerRosters.save(tournament1.toPlayerRoster())

        val result = service.findPlayer(tournament1.id, giorno.id, requestedOn = { today })

        result shouldBe null
    }

    @Test
    fun `should find player with missing score`() {
        playerRosters.save(tournament1.toPlayerRoster(Players.jolyne, Players.giorno, Players.joseph))

        val result = service.findPlayer(tournament1.id, giorno.id, requestedOn = { today })

        result shouldBe PlayerOverviews.giorno.copy(score = 0.points)
    }

    @Test
    fun `should find player with score`() {
        playerRosters.save(tournament1.toPlayerRoster(Players.jolyne, Players.giorno, Players.joseph))
        scoreboardEntries.save(ScoreboardEntry.new(tournament1.id, giorno.id).update(12.points))

        val result = service.findPlayer(tournament1.id, giorno.id, requestedOn = { today })

        result shouldBe PlayerOverviews.giorno.copy(score = 12.points)
    }
}
