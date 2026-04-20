package fr.sdecout.repository.domain.core.user

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CityTest {
    @Test
    fun `should fail to initialize from name with less than 2 characters`() {
        shouldThrow<IllegalArgumentException> { City.from("a") }
            .message shouldBe "City name must have at least 2 characters"
    }

    @Test
    fun `should render as string`() {
        City.from("PARIS").toString() shouldBe "PARIS"
    }
}
