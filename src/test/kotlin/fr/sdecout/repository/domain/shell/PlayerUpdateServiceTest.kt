package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.TestData.Players
import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
import fr.sdecout.repository.domain.TestData.Tournaments.tournament2
import fr.sdecout.repository.domain.TestData.Users.giorno
import fr.sdecout.repository.domain.TestData.Users.joseph
import fr.sdecout.repository.domain.TestData.today
import fr.sdecout.repository.domain.core.roster.PlayerRosterEntry
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.UserId
import fr.sdecout.repository.domain.spi.InMemoryPlayerRosterEntries
import fr.sdecout.repository.domain.spi.InMemoryTournaments
import fr.sdecout.repository.domain.spi.InMemoryUsers
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class PlayerUpdateServiceTest {
    val users = InMemoryUsers()
    val tournaments = InMemoryTournaments()
    val playerRosterEntries = InMemoryPlayerRosterEntries()

    val service = PlayerUpdateService(users, tournaments, playerRosterEntries)

    @AfterEach
    fun afterEach() {
        users.clear()
        tournaments.clear()
        playerRosterEntries.clear()
    }

    @Test
    fun `should reset player roster`() {
        playerRosterEntries.save(PlayerRosterEntry.from(tournament1.id, Players.jolyne.userId, Players.jolyne.nickname))
        playerRosterEntries.save(PlayerRosterEntry.from(tournament1.id,  Players.giorno.userId, Players.giorno.nickname))
        playerRosterEntries.save(PlayerRosterEntry.from(tournament1.id,  Players.joseph.userId, Players.joseph.nickname))

        service.resetPlayerRoster(tournament1.id)

        playerRosterEntries.findAll(tournament1.id) shouldBe emptyList()
    }

    @Test
    fun `should fail to add player from unknown user`() {
        val unknownUserId = UserId.from("unknown-user")

        shouldThrow<DomainExceptions.UserNotFound> {
            service.addPlayer(tournament1.id, unknownUserId, addedOn = { today })
        }.message shouldBe "No user found with id $unknownUserId"
    }

    @Test
    fun `should fail to add player to unknown tournament`() {
        users.save(giorno)
        val unknownTournamentId = TournamentId.from("unknown-tournament")

        shouldThrow<DomainExceptions.TournamentNotFound> {
            service.addPlayer(unknownTournamentId, giorno.id, addedOn = { today })
        }.message shouldBe "No tournament found with id $unknownTournamentId"
    }

    @Test
    fun `should fail to add player that is already in roster`() {
        users.save(joseph)
        tournaments.save(tournament1)
        playerRosterEntries.save(PlayerRosterEntry.from(tournament1.id,  Players.jolyne.userId, Players.jolyne.nickname))
        playerRosterEntries.save(PlayerRosterEntry.from(tournament1.id,  Players.joseph.userId, Players.joseph.nickname))

        shouldThrow<DomainExceptions.DuplicatePlayer> {
            service.addPlayer(tournament1.id, joseph.id, addedOn = { today })
        }.message shouldBe "Tournament with id ${tournament1.id} already includes a player with id ${joseph.id}"
    }

    @Test
    fun `should add player to new roster`() {
        users.save(giorno)
        tournaments.save(tournament2)

        service.addPlayer(tournament2.id, giorno.id, addedOn = { today })

        playerRosterEntries.findAll(tournament2.id) shouldBe listOf(
            PlayerRosterEntry.from(tournament2.id,  Players.giorno.userId, Players.giorno.nickname),
        )
    }

    @Test
    fun `should add player to existing roster`() {
        users.save(giorno)
        tournaments.save(tournament2)
        playerRosterEntries.save(PlayerRosterEntry.from(tournament2.id,  Players.jolyne.userId, Players.jolyne.nickname))

        service.addPlayer(tournament2.id, giorno.id, addedOn = { today })

        playerRosterEntries.findAll(tournament2.id) shouldBe listOf(
            PlayerRosterEntry.from(tournament2.id,  Players.jolyne.userId, Players.jolyne.nickname),
            PlayerRosterEntry.from(tournament2.id,  Players.giorno.userId, Players.giorno.nickname),
        )
    }

    @Test
    fun `should add player that is already in scoreboard`() {
        users.save(giorno)
        tournaments.save(tournament2)
        playerRosterEntries.save(PlayerRosterEntry.from(tournament2.id,  Players.jolyne.userId, Players.jolyne.nickname))

        service.addPlayer(tournament2.id, giorno.id, addedOn = { today })

        playerRosterEntries.findAll(tournament2.id) shouldBe listOf(
            PlayerRosterEntry.from(tournament2.id,  Players.jolyne.userId, Players.jolyne.nickname),
            PlayerRosterEntry.from(tournament2.id,  Players.giorno.userId, Players.giorno.nickname),
        )
    }

}
