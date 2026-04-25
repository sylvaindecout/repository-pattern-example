package fr.sdecout.repository.domain

import fr.sdecout.repository.domain.TestData.PlayerOverviews
import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
import fr.sdecout.repository.domain.TestData.Users.jotaro
import fr.sdecout.repository.domain.TestData.today
import fr.sdecout.repository.domain.api.*
import fr.sdecout.repository.domain.core.scoreboard.Score.Companion.points
import fr.sdecout.repository.domain.shell.*
import fr.sdecout.repository.domain.spi.Alerting
import fr.sdecout.repository.domain.spi.InMemoryPlayerRosters
import fr.sdecout.repository.domain.spi.InMemoryTournaments
import fr.sdecout.repository.domain.spi.InMemoryUsers
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("Scaffolding")
class UserJourneyTest {
    // driven ports
    val users = InMemoryUsers()
    val tournaments = InMemoryTournaments()
    val playerRosters = InMemoryPlayerRosters()
    val alerting = mockk<Alerting>(relaxed = true)

    // driving ports
    val upsertUser: UpsertUser = UserUpdateService(users)
    val findUser: FindUser = UserAccessService(users)
    val upsertTournament: UpsertTournament = TournamentUpdateService(tournaments)
    val findTournament: FindTournament = TournamentAccessService(tournaments)
    val addPlayer: AddPlayer = PlayerUpdateService(users, tournaments, playerRosters, alerting)
    val updateScore: UpdateScore = PlayerUpdateService(users, tournaments, playerRosters, alerting)
    val findPlayer: FindPlayer = PlayerAccessService(users, tournaments, playerRosters)
    val listPlayers: ListPlayers = PlayerAccessService(users, tournaments, playerRosters)
    val resetPlayerRoster: ResetPlayerRoster = PlayerUpdateService(users, tournaments, playerRosters, alerting)

    @Test
    fun `should support user journey`() {
        // back office - configure user
        upsertUser(jotaro)
        findUser(jotaro.id) shouldBe jotaro

        // back office - configure tournament
        upsertTournament(tournament1)
        findTournament(tournament1.id) shouldBe tournament1

        // configure player info
        val player = PlayerOverviews.jotaro

        // select a tournament
        val selectedTournamentId = tournament1.id

        // complete 1st challenge
        addPlayer(selectedTournamentId, jotaro.id, addedOn = { today })
        updateScore(selectedTournamentId, player.userId, 12.points)
        findPlayer(selectedTournamentId, player.userId, requestedOn = { today }) shouldBe player.copy(score = 12.points)

        // complete 2nd challenge
        updateScore(selectedTournamentId, player.userId, 34.points)
        findPlayer(selectedTournamentId, player.userId, requestedOn = { today }) shouldBe player.copy(score = 34.points)

        // complete session
        listPlayers(selectedTournamentId, requestedOn = { today }) shouldBe listOf(player.copy(score = 34.points))
        resetPlayerRoster(selectedTournamentId)
        listPlayers(selectedTournamentId, requestedOn = { today }) shouldBe emptyList()
        findPlayer(selectedTournamentId, player.userId, requestedOn = { today }) shouldBe null
    }
}
