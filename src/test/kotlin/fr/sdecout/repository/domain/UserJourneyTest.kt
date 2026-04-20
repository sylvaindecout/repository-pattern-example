package fr.sdecout.repository.domain

import fr.sdecout.repository.domain.TestData.Users.jotaro
import fr.sdecout.repository.domain.api.FindUser
import fr.sdecout.repository.domain.api.UpsertUser
import fr.sdecout.repository.domain.shell.UserAccessService
import fr.sdecout.repository.domain.shell.UserUpdateService
import fr.sdecout.repository.domain.spi.InMemoryUsers
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("Scaffolding")
class UserJourneyTest {
    // driven ports
    val users = InMemoryUsers()

    // driving ports
    val upsertUser: UpsertUser = UserUpdateService(users)
    val findUser: FindUser = UserAccessService(users)

    @Test
    fun `should support user journey`() {
        // back office - configure user
        upsertUser(jotaro)
        findUser(jotaro.id) shouldBe jotaro
    }
}
