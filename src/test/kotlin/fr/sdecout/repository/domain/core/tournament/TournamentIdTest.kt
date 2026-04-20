package fr.sdecout.repository.domain.core.tournament

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TournamentIdTest {
    @Test
    fun `should fail to initialize from value including white space`() {
        shouldThrow<IllegalArgumentException> { TournamentId.from("a b") }
            .message shouldBe "Tournament ID must not contain white spaces"
    }

    @Test
    fun `should render as string`() {
        val value = "019b41c9-f36e-7f2d-bcd0-50c3e5729eda"
        TournamentId.from(value).toString() shouldBe value
    }
}
