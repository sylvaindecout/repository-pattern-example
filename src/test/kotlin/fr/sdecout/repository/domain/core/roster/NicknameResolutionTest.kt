package fr.sdecout.repository.domain.core.roster

import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
import fr.sdecout.repository.domain.TestData.Users.giorno
import fr.sdecout.repository.domain.TestData.Users.jotaro
import fr.sdecout.repository.domain.core.user.Nickname
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class NicknameResolutionTest {

    @Test
    fun `should use preferred nickname if available`() {
        val preferredNickname = Nickname.from("iggy")
        val roster = emptyList<PlayerRosterEntry>()

        val nickname = roster.availableNicknameClosestTo(preferredNickname)

        nickname shouldBe preferredNickname
    }

    @Test
    fun `should suffix preferred nickname with number if unavailable`() {
        val preferredNickname = Nickname.from("iggy")
        val conflictingPlayer = PlayerRosterEntry.from(tournament1.id, giorno.id, preferredNickname)
        val anotherConflictingPlayer = PlayerRosterEntry.from(tournament1.id, jotaro.id, preferredNickname + "-1")
        val roster = listOf(conflictingPlayer, anotherConflictingPlayer)

        val nickname = roster.availableNicknameClosestTo(preferredNickname)

        nickname shouldBe preferredNickname + "-2"
    }

}
