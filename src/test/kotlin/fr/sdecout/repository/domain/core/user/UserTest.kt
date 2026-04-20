package fr.sdecout.repository.domain.core.user

import fr.sdecout.repository.domain.TestData.Users.giorno
import fr.sdecout.repository.domain.core.user.Age.Companion.yearsOld
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDate

class UserTest {
    @Test
    fun `should resolve age from date of birth`() {
        val today = LocalDate.parse("2025-01-03")
        val dateOfBirth = LocalDate.parse("1985-01-03")
        val user = User(giorno.id, giorno.preferredNickname, dateOfBirth, giorno.city)
        user.age { today } shouldBe 40.yearsOld
    }
}
