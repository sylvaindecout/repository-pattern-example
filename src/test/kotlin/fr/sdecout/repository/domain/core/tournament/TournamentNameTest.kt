package fr.sdecout.repository.domain.core.tournament

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TournamentNameTest {
    @Test
    fun `should fail to initialize from blank value`() {
        shouldThrow<IllegalArgumentException> { TournamentName.from(" ") }
            .message shouldBe "Tournament name must not be blank"
    }

    @Test
    fun `should remove leading and trailing whitespaces on initialization`() {
        TournamentName.from("  Sunday tournament   ") shouldBe TournamentName.from("Sunday tournament")
    }

    @Test
    fun `should render as string`() {
        TournamentName.from("Sunday tournament").toString() shouldBe "Sunday tournament"
    }
}
