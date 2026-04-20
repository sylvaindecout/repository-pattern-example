package fr.sdecout.repository.infra

import fr.sdecout.repository.TestApp
import fr.sdecout.repository.domain.TestData.Users.giorno
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Tag("Acceptance")
@SpringBootTest(classes = [TestApp::class])
@AutoConfigureMockMvc
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = ["/init_users.sql"])
@Sql(executionPhase = AFTER_TEST_METHOD, scripts = ["/clear_data.sql"])
class UserEndpointsTest {

    @Test
    fun `should fail to find an unknown user`(@Autowired mockMvc: MockMvc) {
        val userId = "XXX"
        mockMvc.perform(get("/users/{userId}", userId))
            .andExpect(status().isNotFound())
    }

    @Test
    fun `should find an existing user`(@Autowired mockMvc: MockMvc) {
        val userId = giorno.id.value
        val userAsJson = """{
          "id":"019cc9df-0a49-7fe8-9265-fd00996bd267",
          "preferredNickname":"giorno",
          "dateOfBirth":"1985-04-16",
          "city":"naples"
        }"""
        mockMvc.perform(get("/users/{userId}", userId))
            .andExpect(status().isOk())
            .andExpect(content().json(userAsJson))
    }

    @Test
    fun `should create a new user`(@Autowired mockMvc: MockMvc) {
        val userId = "new-id"
        val createdUserAsJson = """{
          "id":"$userId",
          "preferredNickname":"giorno",
          "dateOfBirth":"1985-04-16",
          "city":"naples"
        }"""
        mockMvc.perform(
            put("/users/{userId}", userId)
                .contentType(APPLICATION_JSON)
                .content(createdUserAsJson)
        ).andExpect(status().isNoContent())
        mockMvc.perform(get("/users/{userId}", userId))
            .andExpect(status().isOk())
            .andExpect(content().json(createdUserAsJson))
    }

    @Test
    fun `should update an existing user`(@Autowired mockMvc: MockMvc) {
        val userId = giorno.id.value
        val updatedUserAsJson = """{
          "id":"$userId",
          "preferredNickname":"giorno2000",
          "dateOfBirth":"2015-04-16",
          "city":"shanghai"
        }"""
        mockMvc.perform(
            put("/users/{userId}", userId)
                .contentType(APPLICATION_JSON)
                .content(updatedUserAsJson)
        ).andExpect(status().isNoContent())
        mockMvc.perform(get("/users/{userId}", userId))
            .andExpect(status().isOk())
            .andExpect(content().json(updatedUserAsJson))
    }

}
