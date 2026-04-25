package fr.sdecout.repository.domain

import fr.sdecout.repository.domain.TestData.PlayerOverviews
import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
import fr.sdecout.repository.domain.TestData.Users.jotaro
import fr.sdecout.repository.domain.TestData.today
import fr.sdecout.repository.domain.api.*
import fr.sdecout.repository.domain.core.scoreboard.Score.Companion.points
import fr.sdecout.repository.domain.core.search.TournamentSearchCriteria.Companion.and
import fr.sdecout.repository.domain.core.search.TournamentSearchCriteria.Companion.where
import fr.sdecout.repository.domain.core.search.TournamentSearchCriterion.Companion.accessibleForAge
import fr.sdecout.repository.domain.core.search.TournamentSearchCriterion.Companion.openSlotsOnly
import fr.sdecout.repository.domain.core.search.TournamentSearchResult
import fr.sdecout.repository.domain.core.search.TournamentSearchResultItem
import fr.sdecout.repository.domain.core.tournament.RosterSize.Companion.players
import fr.sdecout.repository.domain.shell.*
import fr.sdecout.repository.domain.spi.*
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
    val scoreboardEntries = InMemoryScoreboardEntries()
    val tournamentSearchResultItems = CompositeTournamentSearchResultItems(users, tournaments, playerRosters)
    val alerting = mockk<Alerting>(relaxed = true)

    // driving ports
    val upsertUser: UpsertUser = UserUpdateService(users)
    val findUser: FindUser = UserAccessService(users)
    val upsertTournament: UpsertTournament = TournamentUpdateService(tournaments)
    val findTournament: FindTournament = TournamentAccessService(tournaments, tournamentSearchResultItems)
    val searchTournaments: SearchTournaments = TournamentAccessService(tournaments, tournamentSearchResultItems)
    val addPlayer: AddPlayer = PlayerUpdateService(users, tournaments, playerRosters, scoreboardEntries, alerting)
    val updateScore: UpdateScore = PlayerUpdateService(users, tournaments, playerRosters, scoreboardEntries, alerting)
    val findPlayer: FindPlayer = PlayerAccessService(users, tournaments, playerRosters, scoreboardEntries)
    val listPlayers: ListPlayers = PlayerAccessService(users, tournaments, playerRosters, scoreboardEntries)
    val resetPlayerRoster: ResetPlayerRoster = PlayerUpdateService(users, tournaments, playerRosters, scoreboardEntries, alerting)

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
        val eligibleTournaments = searchTournaments(
            where(accessibleForAge(player.age)) and openSlotsOnly(),
            requestedOn = { today }
        )
        eligibleTournaments shouldBe TournamentSearchResult.of(
            TournamentSearchResultItem(
                id = tournament1.id,
                name = tournament1.name,
                maxPlayerRosterSize = tournament1.maxPlayerRosterSize,
                playerRostersSize = 0.players,
                minimumAge = tournament1.minimumAge,
                averageAge = null,
            ),
        )
        val selectedTournamentId = eligibleTournaments.items.single().id

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
