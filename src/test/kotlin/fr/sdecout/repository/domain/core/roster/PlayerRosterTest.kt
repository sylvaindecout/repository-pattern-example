package fr.sdecout.repository.domain.core.roster

import fr.sdecout.repository.domain.TestData.Players.giorno
import fr.sdecout.repository.domain.TestData.Players.jolyne
import fr.sdecout.repository.domain.TestData.Players.joseph
import fr.sdecout.repository.domain.TestData.Players.jotaro
import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
import fr.sdecout.repository.domain.core.roster.PlayerRoster.Companion.toPlayerRoster
import fr.sdecout.repository.domain.core.tournament.RosterSize.Companion.players
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class PlayerRosterTest {
    @Test
    fun `should initialize empty roster`() {
        tournament1.toPlayerRoster() should {
            it.tournamentId shouldBe tournament1.id
            it.players shouldBe emptyList()
        }
    }

    @Test
    fun `should initialize roster with players`() {
        tournament1.toPlayerRoster(giorno, jotaro) should {
            it.tournamentId shouldBe tournament1.id
            it.players shouldBe listOf(giorno, jotaro)
        }
    }

    @Test
    fun `should resolve size of the roster`() {
        val roster = tournament1.toPlayerRoster(giorno, jotaro)
        roster.size shouldBe 2.players
    }

    @Test
    fun `should identify roster as full`() {
        val tournament = tournament1.copy(maxPlayerRosterSize = 2.players)
        val roster = tournament.toPlayerRoster(giorno, jotaro)
        roster.isFull shouldBe true
    }

    @Test
    fun `should not identify roster with remaining slots as full`() {
        val tournament = tournament1.copy(maxPlayerRosterSize = 3.players)
        val roster = tournament.toPlayerRoster(giorno, jotaro)
        roster.isFull shouldBe false
    }

    @Test
    fun `should find player from their ID`() {
        val roster = tournament1.toPlayerRoster(giorno, jotaro)
        roster[jotaro.userId] shouldBe jotaro
    }

    @Test
    fun `should not find missing player from their ID`() {
        val roster = tournament1.toPlayerRoster(giorno, jotaro)
        roster[jolyne.userId] shouldBe null
    }

    @Test
    fun `should add new player from their ID`() {
        val roster = tournament1.toPlayerRoster(giorno, jotaro)
        roster.add(joseph) shouldBeIgnoringPendingPlayers tournament1.toPlayerRoster(giorno, jotaro, joseph)
    }

    @Test
    fun `should fail to add new player from duplicate ID`() {
        val roster = tournament1.toPlayerRoster(giorno, jotaro)
        shouldThrow<IllegalArgumentException> { roster.add(jotaro) }
            .message shouldBe "Player roster must not include duplicate users"
    }

    private infix fun PlayerRoster?.shouldBeIgnoringPendingPlayers(expected: PlayerRoster) = shouldNotBeNull()
        .shouldBeEqualToIgnoringFields(expected, PlayerRoster::pendingPlayers)
}
