package fr.sdecout.repository.infra

import fr.sdecout.repository.TestApp
import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
import fr.sdecout.repository.domain.TestData.Tournaments.tournament2
import fr.sdecout.repository.domain.TestData.Users.giorno
import fr.sdecout.repository.domain.TestData.Users.jolyne
import fr.sdecout.repository.domain.TestData.Users.joseph
import fr.sdecout.repository.domain.TestData.Users.jotaro
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@Tag("Acceptance")
@SpringBootTest(classes = [TestApp::class])
@AutoConfigureMockMvc
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = ["/init_users.sql", "/init_tournaments.sql", "/init_rosters.sql"])
@Sql(executionPhase = AFTER_TEST_METHOD, scripts = ["/clear_data.sql"])
class RosterEndpointsTest {

    @Test
    fun `should fail to list players of an unknown tournament`(@Autowired mockMvc: MockMvc) {
        val tournamentId = "XXX"
        mockMvc.perform(get("/tournaments/{tournamentId}/players", tournamentId))
            .andExpect(status().isNotFound())
    }

    @Test
    fun `should list players of an existing tournament`(@Autowired mockMvc: MockMvc) {
        val tournamentId = tournament1.id.value
        mockMvc.perform(get("/tournaments/{tournamentId}/players", tournamentId))
            .andExpect(status().isOk())
            .andExpect(
                content().json(
                    """[
                    {
                        "id": "019cc9df-0a49-7fe8-9265-fd00996bd267",
                        "nickname": "giorno"
                    },
                    {
                        "id": "019cc9df-a40f-7dcf-9ed5-ab5d27ab9ff1",
                        "nickname": "jotaro"
                    }
                ]"""
                )
            )
    }

    @Test
    fun `should fail to add a player to an unknown tournament`(@Autowired mockMvc: MockMvc) {
        val tournamentId = "XXX"
        val userId = giorno.id.value
        mockMvc.perform(
            post("/tournaments/{tournamentId}/players", tournamentId)
                .contentType(APPLICATION_JSON)
                .content("""{ "userId": "$userId" }""")
        ).andExpect(status().isNotFound())
    }

    @Test
    fun `should fail to add an unknown player to a tournament`(@Autowired mockMvc: MockMvc) {
        val tournamentId = tournament1.id.value
        val userId = "XXX"
        mockMvc.perform(
            post("/tournaments/{tournamentId}/players", tournamentId)
                .contentType(APPLICATION_JSON)
                .content("""{ "userId": "$userId" }""")
        ).andExpect(status().isNotFound())
    }

    @Test
    fun `should add a player to an existing tournament`(@Autowired mockMvc: MockMvc) {
        val userId = joseph.id.value
        val tournamentId = tournament1.id.value
        mockMvc.perform(
            post("/tournaments/{tournamentId}/players", tournamentId)
                .contentType(APPLICATION_JSON)
                .content("""{ "userId": "$userId" }""")
        ).andExpect(status().isCreated())
            .andExpect(header().string(LOCATION, "/tournaments/$tournamentId/players/$userId"))
    }

    @Test
    fun `should clear players of a tournament`(@Autowired mockMvc: MockMvc) {
        val tournamentId = tournament1.id.value
        mockMvc.perform(
            delete("/tournaments/{tournamentId}/players", tournamentId)
        ).andExpect(status().isNoContent())
        mockMvc.perform(get("/tournaments/{tournamentId}/players", tournamentId))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"))
    }

}
