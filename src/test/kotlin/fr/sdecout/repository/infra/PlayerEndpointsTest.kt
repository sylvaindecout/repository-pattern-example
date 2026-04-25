package fr.sdecout.repository.infra

import fr.sdecout.repository.TestApp
import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@Tag("Acceptance")
@SpringBootTest(classes = [TestApp::class])
@AutoConfigureMockMvc
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = ["/init_users.sql", "/init_tournaments.sql", "/init_rosters.sql"])
@Sql(executionPhase = AFTER_TEST_METHOD, scripts = ["/clear_data.sql"])
class PlayerEndpointsTest {

    @Test
    fun `should fail to find an unknown player`(@Autowired mockMvc: MockMvc) {
        val tournamentId = tournament1.id.value
        val playerId = "XXX"
        mockMvc.perform(get("/tournaments/{tournamentId}/players/{playerId}", tournamentId, playerId))
            .andExpect(status().isNotFound())
    }

    @Test
    fun `should find an existing player`(@Autowired mockMvc: MockMvc) {
        val tournamentId = tournament1.id.value
        val playerId = giorno.id.value
        mockMvc.perform(get("/tournaments/{tournamentId}/players/{playerId}", tournamentId, playerId))
            .andExpect(status().isOk())
            .andExpect(
                content().json(
                    """{
                        "id": "019cc9df-0a49-7fe8-9265-fd00996bd267",
                        "nickname": "giorno",
                        "age": 12,
                        "city": "naples",
                        "score": 42
                    }"""
                )
            )
    }

    @Test
    fun `should update the score of an existing player`(@Autowired mockMvc: MockMvc) {
        val tournamentId = tournament1.id.value
        val playerId = giorno.id.value
        mockMvc.perform(
            put("/tournaments/{tournamentId}/players/{playerId}/score", tournamentId, playerId)
                .contentType(APPLICATION_JSON)
                .content("""{ "score": 2500 }""")
        ).andExpect(status().isNoContent())
        mockMvc.perform(get("/tournaments/{tournamentId}/players/{playerId}", tournamentId, playerId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.score").value(2500))
    }

}
