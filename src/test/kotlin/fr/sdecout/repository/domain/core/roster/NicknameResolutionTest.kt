package fr.sdecout.repository.domain.core.roster

import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
import fr.sdecout.repository.domain.TestData.Users.giorno
import fr.sdecout.repository.domain.TestData.Users.jotaro
import fr.sdecout.repository.domain.core.roster.PlayerRoster.Companion.toPlayerRoster
import fr.sdecout.repository.domain.core.user.Nickname
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class NicknameResolutionTest {

    @Test
    fun `should use preferred nickname if available`() {
        val preferredNickname = Nickname.from("iggy")
        val roster = tournament1.toPlayerRoster()

        val nickname = roster.availableNicknameClosestTo(preferredNickname)

        nickname shouldBe preferredNickname
    }

    @Test
    fun `should suffix preferred nickname with number if unavailable`() {
        val preferredNickname = Nickname.from("iggy")
        val conflictingPlayer = Player(giorno.id, preferredNickname)
        val anotherConflictingPlayer = Player(jotaro.id, preferredNickname + "-1")
        val roster = tournament1.toPlayerRoster(conflictingPlayer, anotherConflictingPlayer)

        val nickname = roster.availableNicknameClosestTo(preferredNickname)

        nickname shouldBe preferredNickname + "-2"
    }

}
