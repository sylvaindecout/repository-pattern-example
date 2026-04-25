package fr.sdecout.repository.domain.core.scoreboard

import fr.sdecout.repository.domain.TestData.Tournaments.tournament1
import fr.sdecout.repository.domain.TestData.Users.giorno
import fr.sdecout.repository.domain.core.scoreboard.Score.Companion.points
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ScoreboardEntryTest {
    @Test
    fun `should initialize with 0 points`() {
        ScoreboardEntry.new(tournament1.id, giorno.id) should {
            it.tournamentId shouldBe tournament1.id
            it.userId shouldBe giorno.id
            it.score shouldBe 0.points
        }
    }

    @Test
    fun `should update score`() {
        val entry = ScoreboardEntry.new(tournament1.id, giorno.id)
        entry.update(45.points) should {
            it.tournamentId shouldBe tournament1.id
            it.userId shouldBe giorno.id
            it.score shouldBe 45.points
        }
    }
}
