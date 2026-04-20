package fr.sdecout.repository.domain.core.user

import fr.sdecout.repository.domain.core.user.Age.Companion.toAge
import fr.sdecout.repository.domain.core.user.Age.Companion.yearsOld
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AgeTest {
    @Test
    fun `should fail to initialize from negative value`() {
        shouldThrow<IllegalArgumentException> { (-1).yearsOld }
            .message shouldBe "Age must not be negative"
    }

    @Test
    fun `should resolve age from date of birth on birthday`() {
        val dateOfBirth = LocalDate.parse("1985-01-03")
        val today = LocalDate.parse("2025-01-03")
        dateOfBirth.toAge { today } shouldBe 40.yearsOld
    }

    @Test
    fun `should resolve age from date of birth on day before birthday`() {
        val dateOfBirth = LocalDate.parse("1985-01-03")
        val today = LocalDate.parse("2025-01-02")
        dateOfBirth.toAge { today } shouldBe 39.yearsOld
    }

    @Test
    fun `should sort by value`() {
        listOf(40.yearsOld, 1.yearsOld, 20.yearsOld, 1.yearsOld).sorted() shouldBe
                listOf(1.yearsOld, 1.yearsOld, 20.yearsOld, 40.yearsOld)
    }

    @Test
    fun `should render as string`() {
        0.yearsOld.toString() shouldBe "0 years old"
    }
}
