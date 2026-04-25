package fr.sdecout.repository.domain.spi

import fr.sdecout.repository.domain.TestData.PlayerOverviews
import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
import fr.sdecout.repository.domain.TestData.Tournaments.tournament2
import fr.sdecout.repository.domain.TestData.Users.giorno
import fr.sdecout.repository.domain.TestData.Users.jolyne
import fr.sdecout.repository.domain.TestData.Users.joseph
import fr.sdecout.repository.domain.TestData.Users.jotaro
import fr.sdecout.repository.domain.TestData.today
import fr.sdecout.repository.domain.core.player.PlayerOverview
import fr.sdecout.repository.domain.core.roster.PlayerRoster.Companion.toPlayerRoster
import fr.sdecout.repository.domain.core.search.TournamentSearchCriteria.Companion.all
import fr.sdecout.repository.domain.core.search.TournamentSearchCriteria.Companion.and
import fr.sdecout.repository.domain.core.search.TournamentSearchCriteria.Companion.where
import fr.sdecout.repository.domain.core.search.TournamentSearchCriterion.Companion.accessibleForAge
import fr.sdecout.repository.domain.core.search.TournamentSearchCriterion.Companion.openSlotsOnly
import fr.sdecout.repository.domain.core.search.TournamentSearchResultItem
import fr.sdecout.repository.domain.core.tournament.RosterSize.Companion.players
import fr.sdecout.repository.domain.core.tournament.Tournament
import fr.sdecout.repository.domain.core.user.Age.Companion.average
import fr.sdecout.repository.domain.core.user.Age.Companion.yearsOld
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CompositeTournamentSearchResultItemsTest {
    val users = InMemoryUsers()
    val tournaments = InMemoryTournaments()
    val playerRosters = InMemoryPlayerRosters()

    val tournamentSearchResultItems = CompositeTournamentSearchResultItems(users, tournaments, playerRosters)

    val nsfwTournament = tournament1.copy(minimumAge = 18.yearsOld)
    val fullTournament = tournament2.copy(minimumAge = null, maxPlayerRosterSize = 2.players)
    val playersInNsfwTournament = listOf(PlayerOverviews.jolyne, PlayerOverviews.giorno, PlayerOverviews.joseph)
    val playersInFullTournament = listOf(PlayerOverviews.joseph, PlayerOverviews.jotaro)

    @BeforeEach
    fun beforeEach() {
        users.save(jolyne)
        users.save(giorno)
        users.save(joseph)
        users.save(jotaro)
        tournaments.save(nsfwTournament)
        tournaments.save(fullTournament)
        playerRosters.save(nsfwTournament.toPlayerRoster(playersInNsfwTournament))
        playerRosters.save(fullTournament.toPlayerRoster(playersInFullTournament))
    }

    @Test
    fun `should search tournaments with no filter`() {
        val result = tournamentSearchResultItems.findAll(all(), requestedOn = { today })
        result shouldContainExactly listOf(
            TournamentSearchResultItem(
                id = nsfwTournament.id,
                name = nsfwTournament.name,
                maxPlayerRosterSize = nsfwTournament.maxPlayerRosterSize,
                playerRostersSize = playersInNsfwTournament.size.players,
                minimumAge = nsfwTournament.minimumAge,
                averageAge = playersInNsfwTournament.averageAge(),
            ),
            TournamentSearchResultItem(
                id = fullTournament.id,
                name = fullTournament.name,
                maxPlayerRosterSize = fullTournament.maxPlayerRosterSize,
                playerRostersSize = playersInFullTournament.size.players,
                minimumAge = fullTournament.minimumAge,
                averageAge = playersInFullTournament.averageAge(),
            ),
        )
    }

    @Test
    fun `should exclude tournaments with age limit that is not met`() {
        val result = tournamentSearchResultItems.findAll(where(accessibleForAge(16.yearsOld)), requestedOn = { today })
        result shouldContainExactly listOf(
            TournamentSearchResultItem(
                id = fullTournament.id,
                name = fullTournament.name,
                maxPlayerRosterSize = fullTournament.maxPlayerRosterSize,
                playerRostersSize = fullTournament.toPlayerRoster(playersInFullTournament).size,
                minimumAge = fullTournament.minimumAge,
                averageAge = playersInFullTournament.averageAge(),
            ),
        )
    }

    @Test
    fun `should exclude tournaments with no open slots`() {
        val result = tournamentSearchResultItems.findAll(where(openSlotsOnly()), requestedOn = { today })
        result shouldContainExactly listOf(
            TournamentSearchResultItem(
                id = nsfwTournament.id,
                name = nsfwTournament.name,
                maxPlayerRosterSize = nsfwTournament.maxPlayerRosterSize,
                playerRostersSize = playersInNsfwTournament.size.players,
                minimumAge = nsfwTournament.minimumAge,
                averageAge = playersInNsfwTournament.averageAge(),
            ),
        )
    }

    @Test
    fun `should exclude tournaments with several filters`() {
        val result = tournamentSearchResultItems.findAll(where(accessibleForAge(16.yearsOld)) and openSlotsOnly(), requestedOn = { today })
        result shouldBe emptyList()
    }

    private fun Tournament.toPlayerRoster(players: List<PlayerOverview>) =
        toPlayerRoster(*players.map { it.player }.toTypedArray())

    private fun List<PlayerOverview>.averageAge() = map { it.age }.average()
}
