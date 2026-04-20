package fr.sdecout.repository.domain.core.tournament

import fr.sdecout.repository.domain.core.tournament.RosterSize.Companion.players
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RosterSizeTest {
    @Test
    fun `should fail to initialize from negative value`() {
        shouldThrow<IllegalArgumentException> { (-1).players }
            .message shouldBe "Roster size must not be negative"
    }

    @Test
    fun `should compare`() {
        (40.players < 39.players) shouldBe false
    }

    @Test
    fun `should render as string`() {
        40.players.toString() shouldBe "40 players"
    }
}
