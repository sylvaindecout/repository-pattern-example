package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.TestData.Players
import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
import fr.sdecout.repository.domain.TestData.Tournaments.tournament2
import fr.sdecout.repository.domain.TestData.Users.giorno
import fr.sdecout.repository.domain.TestData.Users.jolyne
import fr.sdecout.repository.domain.TestData.Users.joseph
import fr.sdecout.repository.domain.TestData.today
import fr.sdecout.repository.domain.core.roster.Player
import fr.sdecout.repository.domain.core.roster.PlayerRoster
import fr.sdecout.repository.domain.core.roster.PlayerRoster.Companion.toPlayerRoster
import fr.sdecout.repository.domain.core.tournament.RosterSize.Companion.players
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.UserId
import fr.sdecout.repository.domain.spi.PlayerRosters
import fr.sdecout.repository.domain.spi.Tournaments
import fr.sdecout.repository.domain.spi.Users
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.MockKMatcherScope
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

/**
 * Issue: Assertions - Verifying that `save` was called is not the same as checking that a state was updated.
 * This is especially true when you have duplicate IDs (overwrite silently).
 */
class PlayerUpdateServiceTest {
    val users = mockk<Users>(relaxed = true)
    val tournaments = mockk<Tournaments>(relaxed = true)
    val playerRosters = mockk<PlayerRosters>(relaxed = true)

    val service = PlayerUpdateService(users, tournaments, playerRosters)

    @Test
    fun `should reset player roster`() {
        service.resetPlayerRoster(tournament1.id)

        verify { playerRosters.remove(tournament1.id) }
    }

    @Test
    fun `should fail to add player from unknown user`() {
        val unknownUserId = UserId.from("unknown-user")
        every { users.find(unknownUserId) } returns null

        shouldThrow<DomainExceptions.UserNotFound> {
            service.addPlayer(tournament1.id, unknownUserId, addedOn = { today })
        }.message shouldBe "No user found with id $unknownUserId"
    }

    @Test
    fun `should fail to add player to unknown tournament`() {
        every { users.find(giorno.id) } returns giorno
        val unknownTournamentId = TournamentId.from("unknown-tournament")
        every { tournaments.find(unknownTournamentId) } returns null
        every { playerRosters.find(unknownTournamentId) } returns null

        shouldThrow<DomainExceptions.TournamentNotFound> {
            service.addPlayer(unknownTournamentId, giorno.id, addedOn = { today })
        }.message shouldBe "No tournament found with id $unknownTournamentId"
    }

    @Test
    fun `should fail to add player if roster is already full`() {
        every { users.find(giorno.id) } returns giorno
        val tournament = tournament1.copy(maxPlayerRosterSize = 1.players)
        every { tournaments.find(tournament.id) } returns tournament
        every { playerRosters.find(tournament.id) } returns tournament.toPlayerRoster(Players.jolyne)

        shouldThrow<DomainExceptions.FullPlayerRoster> {
            service.addPlayer(tournament.id, giorno.id, addedOn = { today })
        }.message shouldBe "Player roster is full for tournament with id ${tournament.id}"
    }

    @Test
    fun `should fail to add player that is already in roster`() {
        every { users.find(joseph.id) } returns joseph
        every { tournaments.find(tournament1.id) } returns tournament1
        every { playerRosters.find(tournament1.id) } returns tournament1.toPlayerRoster(Players.jolyne, Players.joseph)

        shouldThrow<DomainExceptions.DuplicatePlayer> {
            service.addPlayer(tournament1.id, joseph.id, addedOn = { today })
        }.message shouldBe "Tournament with id ${tournament1.id} already includes a player with id ${joseph.id}"
    }

    @Test
    fun `should add player to new roster`() {
        every { users.find(giorno.id) } returns giorno
        every { tournaments.find(tournament2.id) } returns tournament2
        every { playerRosters.find(tournament2.id) } returns tournament2.toPlayerRoster()

        service.addPlayer(tournament2.id, giorno.id, addedOn = { today })

        verify { playerRosters.save(eqIgnoringPendingPlayers(tournament2.toPlayerRoster(Players.giorno))) }
    }

    @Test
    fun `should add player to existing roster`() {
        every { users.find(giorno.id) } returns giorno
        every { tournaments.find(tournament2.id) } returns tournament2
        every { playerRosters.find(tournament2.id) } returns tournament2.toPlayerRoster(Players.jolyne)

        service.addPlayer(tournament2.id, giorno.id, addedOn = { today })

        verify { playerRosters.save(eqIgnoringPendingPlayers(tournament2.toPlayerRoster(Players.jolyne, Players.giorno))) }
    }

    @Test
    fun `should add player that is already in roster with generated nickname`() {
        every { users.find(jolyne.id) } returns jolyne
        every { users.find(joseph.id) } returns joseph
        every { tournaments.find(tournament1.id) } returns tournament1
        val anotherJoseph = Player(jolyne.id, Players.joseph.nickname)
        every { playerRosters.find(tournament1.id) } returns tournament1.toPlayerRoster(anotherJoseph)
        val joseph2 = Player(joseph.id, Players.joseph.nickname + "-1")

        service.addPlayer(tournament1.id, joseph.id, addedOn = { today })

        verify { playerRosters.save(eqIgnoringPendingPlayers(tournament1.toPlayerRoster(anotherJoseph, joseph2))) }
    }

    @Test
    fun `should add player that is already in scoreboard`() {
        every { users.find(giorno.id) } returns giorno
        every { tournaments.find(tournament2.id) } returns tournament2
        every { playerRosters.find(tournament2.id) } returns tournament2.toPlayerRoster(Players.jolyne)

        service.addPlayer(tournament2.id, giorno.id, addedOn = { today })

        verify { playerRosters.save(eqIgnoringPendingPlayers(tournament2.toPlayerRoster(Players.jolyne, Players.giorno))) }
    }

    private fun MockKMatcherScope.eqIgnoringPendingPlayers(expected: PlayerRoster) = match<PlayerRoster> { actual ->
        expected.tournamentId == actual.tournamentId
                && expected.maxPlayerRosterSize == actual.maxPlayerRosterSize
                && expected.minimumAge == actual.minimumAge
                && expected.players.size == actual.players.size
                && expected.players.toSet() == actual.players.toSet()
    }
}
