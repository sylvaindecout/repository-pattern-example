package fr.sdecout.repository.infra

import fr.sdecout.repository.TestApp
import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
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
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = ["/init_tournaments.sql"])
@Sql(executionPhase = AFTER_TEST_METHOD, scripts = ["/clear_data.sql"])
class TournamentEndpointsTest {

    @Test
    fun `should fail to find an unknown tournament`(@Autowired mockMvc: MockMvc) {
        val tournamentId = "XXX"
        mockMvc.perform(get("/tournaments/{tournamentId}", tournamentId))
            .andExpect(status().isNotFound())
    }

    @Test
    fun `should find an existing tournament`(@Autowired mockMvc: MockMvc) {
        val tournamentId = tournament1.id.value
        val tournamentAsJson = """{
          "id":"019b41c9-f36e-7f2d-bcd0-50c3e5729eda",
          "name":"Spring tournament",
          "maxPlayerRosterSize":10,
          "minimumAge":18
        }"""
        mockMvc.perform(get("/tournaments/{tournamentId}", tournamentId))
            .andExpect(status().isOk())
            .andExpect(content().json(tournamentAsJson))
    }

    @Test
    fun `should create a new tournament`(@Autowired mockMvc: MockMvc) {
        val tournamentId = "new-id"
        val createdTournamentAsJson = """{
          "id":"new-id",
          "name":"Tournament #3",
          "maxPlayerRosterSize":10,
          "minimumAge":18
        }"""
        mockMvc.perform(
            put("/tournaments/{tournamentId}", tournamentId)
                .contentType(APPLICATION_JSON)
                .content(createdTournamentAsJson)
        ).andExpect(status().isNoContent())
        mockMvc.perform(get("/tournaments/{tournamentId}", tournamentId))
            .andExpect(status().isOk())
            .andExpect(content().json(createdTournamentAsJson))
    }

    @Test
    fun `should update an existing tournament`(@Autowired mockMvc: MockMvc) {
        val tournamentId = tournament1.id.value
        val updatedTournamentAsJson = """{
          "id":"019b41c9-f36e-7f2d-bcd0-50c3e5729eda",
          "name":"Tournament #10",
          "maxPlayerRosterSize":1000
        }"""
        mockMvc.perform(
            put("/tournaments/{tournamentId}", tournamentId)
                .contentType(APPLICATION_JSON)
                .content(updatedTournamentAsJson)
        ).andExpect(status().isNoContent())
        mockMvc.perform(get("/tournaments/{tournamentId}", tournamentId))
            .andExpect(status().isOk())
            .andExpect(content().json(updatedTournamentAsJson))
    }

}
