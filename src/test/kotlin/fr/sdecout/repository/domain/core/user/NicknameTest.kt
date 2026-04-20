package fr.sdecout.repository.domain.core.user

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class NicknameTest {
    @Test
    fun `should fail to initialize from blank value`() {
        shouldThrow<IllegalArgumentException> { Nickname.from(" ") }
            .message shouldBe "Nickname must not be blank"
    }

    @Test
    fun `should be case insensitive`() {
        Nickname.from("johnny") shouldBe Nickname.from("JohNNy")
    }

    @Test
    fun `should remove leading and trailing whitespaces on initialization`() {
        Nickname.from("  john ny   ") shouldBe Nickname.from("john ny")
    }

    @Test
    fun `should add suffix`() {
        Nickname.from("johnny") + "-1" shouldBe Nickname.from("johnny-1")
    }

    @Test
    fun `should render as string`() {
        Nickname.from("johnny").toString() shouldBe "johnny"
    }
}
