package fr.sdecout.repository.domain.core.user

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class UserIdTest {
    @Test
    fun `should fail to initialize from value including white space`() {
        shouldThrow<IllegalArgumentException> { UserId.from("a b") }
            .message shouldBe "User ID must not contain white spaces"
    }

    @Test
    fun `should render as string`() {
        val value = "019b41c9-f36e-7f2d-bcd0-50c3e5729eda"
        UserId.from(value).toString() shouldBe value
    }
}
