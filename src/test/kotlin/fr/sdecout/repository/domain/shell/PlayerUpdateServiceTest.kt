package fr.sdecout.repository.domain.shell

import fr.sdecout.repository.domain.TestData.Players
import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
import fr.sdecout.repository.domain.TestData.Tournaments.tournament2
import fr.sdecout.repository.domain.TestData.Users.giorno
import fr.sdecout.repository.domain.TestData.Users.jolyne
import fr.sdecout.repository.domain.TestData.Users.joseph
import fr.sdecout.repository.domain.TestData.today
import fr.sdecout.repository.domain.core.alerting.Notification
import fr.sdecout.repository.domain.core.alerting.PriorityLevel.HIGH
import fr.sdecout.repository.domain.core.roster.Player
import fr.sdecout.repository.domain.core.roster.PlayerRoster
import fr.sdecout.repository.domain.core.roster.PlayerRoster.Companion.toPlayerRoster
import fr.sdecout.repository.domain.core.tournament.RosterSize.Companion.players
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.UserId
import fr.sdecout.repository.domain.spi.InMemoryNotifications
import fr.sdecout.repository.domain.spi.InMemoryPlayerRosters
import fr.sdecout.repository.domain.spi.InMemoryTournaments
import fr.sdecout.repository.domain.spi.InMemoryUsers
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class PlayerUpdateServiceTest {
    val users = InMemoryUsers()
    val tournaments = InMemoryTournaments()
    val playerRosters = InMemoryPlayerRosters()
    val notifications = InMemoryNotifications()

    val service = PlayerUpdateService(users, tournaments, playerRosters, notifications)

    @AfterEach
    fun afterEach() {
        users.clear()
        tournaments.clear()
        playerRosters.clear()
    }

    @Test
    fun `should reset player roster`() {
        playerRosters.save(tournament1.toPlayerRoster(Players.jolyne, Players.giorno, Players.joseph))

        service.resetPlayerRoster(tournament1.id)

        playerRosters.find(tournament1.id) shouldBe null
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
    fun `should fail to add player if roster is already full`() {
        users.save(giorno)
        val tournament = tournament1.copy(maxPlayerRosterSize = 1.players)
        tournaments.save(tournament)
        playerRosters.save(tournament.toPlayerRoster(Players.jolyne))

        shouldThrow<DomainExceptions.FullPlayerRoster> {
            service.addPlayer(tournament.id, giorno.id, addedOn = { today })
        }.message shouldBe "Player roster is full for tournament with id ${tournament.id}"
    }

    @Test
    fun `should fail to add player if it breaks age limit of the tournament`() {
        users.save(jolyne)
        val tournament = tournament1
        tournaments.save(tournament)

        shouldThrow<DomainExceptions.BreakingAgeLimit> {
            service.addPlayer(tournament.id, jolyne.id, addedOn = { today })
        }.message shouldBe "Player with nickname ${Players.jolyne.nickname} is too young (6 years old) to register to tournament with id ${tournament.id} (limit: ${tournament.minimumAge})"
        notifications.findAll() shouldContain Notification.of(priority = HIGH, "Player ${Players.jolyne.nickname} (6 years old) tried to register to tournament restricted to ${tournament.minimumAge}+")
    }

    @Test
    fun `should fail to add player that is already in roster`() {
        users.save(joseph)
        tournaments.save(tournament1)
        playerRosters.save(tournament1.toPlayerRoster(Players.jolyne, Players.joseph))

        shouldThrow<DomainExceptions.DuplicatePlayer> {
            service.addPlayer(tournament1.id, joseph.id, addedOn = { today })
        }.message shouldBe "Tournament with id ${tournament1.id} already includes a player with id ${joseph.id}"
    }

    @Test
    fun `should add player to new roster`() {
        users.save(giorno)
        tournaments.save(tournament2)

        service.addPlayer(tournament2.id, giorno.id, addedOn = { today })

        playerRosters.find(tournament2.id) shouldBeIgnoringPendingPlayers tournament2.toPlayerRoster(Players.giorno)
    }

    @Test
    fun `should add player to existing roster`() {
        users.save(giorno)
        tournaments.save(tournament2)
        playerRosters.save(tournament2.toPlayerRoster(Players.jolyne))

        service.addPlayer(tournament2.id, giorno.id, addedOn = { today })

        playerRosters.find(tournament2.id) shouldBeIgnoringPendingPlayers tournament2.toPlayerRoster(Players.jolyne, Players.giorno)
    }

    @Test
    fun `should add player that is already in roster with generated nickname`() {
        users.save(jolyne)
        users.save(joseph)
        tournaments.save(tournament1)
        val anotherJoseph = Player(jolyne.id, Players.joseph.nickname)
        playerRosters.save(tournament1.toPlayerRoster(anotherJoseph))
        val joseph2 = Player(joseph.id, Players.joseph.nickname + "-1")

        service.addPlayer(tournament1.id, joseph.id, addedOn = { today })

        playerRosters.find(tournament1.id) shouldBeIgnoringPendingPlayers tournament1.toPlayerRoster(anotherJoseph, joseph2)
    }

    @Test
    fun `should add player that is already in scoreboard`() {
        users.save(giorno)
        tournaments.save(tournament2)
        playerRosters.save(tournament2.toPlayerRoster(Players.jolyne))

        service.addPlayer(tournament2.id, giorno.id, addedOn = { today })

        playerRosters.find(tournament2.id) shouldBeIgnoringPendingPlayers tournament2.toPlayerRoster(Players.jolyne, Players.giorno)
    }

    private infix fun PlayerRoster?.shouldBeIgnoringPendingPlayers(expected: PlayerRoster) = shouldNotBeNull()
        .shouldBeEqualToIgnoringFields(expected, PlayerRoster::pendingPlayers)
}
