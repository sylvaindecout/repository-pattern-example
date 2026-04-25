package fr.sdecout.repository.infra

import fr.sdecout.repository.TestApp
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Tag("Acceptance")
@SpringBootTest(classes = [TestApp::class])
@AutoConfigureMockMvc
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = ["/init_users.sql", "/init_tournaments.sql", "/init_rosters.sql"])
@Sql(executionPhase = AFTER_TEST_METHOD, scripts = ["/clear_data.sql"])
class SelectionEndpointsTest {

    @Test
    fun `should search tournaments with no criteria`(@Autowired mockMvc: MockMvc) {
        mockMvc.perform(
            post("/tournaments/search")
                .contentType(APPLICATION_JSON)
                .content("""{}""")
        ).andExpect(status().isOk())
            .andExpect(content().json("""
                [
                    {
                      "id":"019b41c9-f36e-7f2d-bcd0-50c3e5729eda",
                      "name":"Spring tournament",
                      "maxPlayerRosterSize":10,
                      "minimumAge":18,
                      "playerRosterSize":2,
                      "averageAge":19
                    },
                    {
                      "id":"019ccebc-7d4d-7a84-8691-a97e0725e8c7",
                      "name":"Summer tournament",
                      "maxPlayerRosterSize":2,
                      "playerRosterSize":0
                    }
                ]"""
            ))
    }

    @Test
    fun `should search tournaments with criteria`(@Autowired mockMvc: MockMvc) {
        mockMvc.perform(
            post("/tournaments/search")
                .contentType(APPLICATION_JSON)
                .content("""{ "age": 16 }""")
        ).andExpect(status().isOk())
            .andExpect(content().json("""
                [
                    {
                      "id":"019ccebc-7d4d-7a84-8691-a97e0725e8c7",
                      "name":"Summer tournament",
                      "maxPlayerRosterSize":2,
                      "playerRosterSize":0
                    }
                ]"""
            ))
    }

}
