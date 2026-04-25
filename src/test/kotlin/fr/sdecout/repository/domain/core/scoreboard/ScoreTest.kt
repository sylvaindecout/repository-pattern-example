package fr.sdecout.repository.domain.core.scoreboard

import fr.sdecout.repository.domain.core.scoreboard.Score.Companion.points
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ScoreTest {
    @Test
    fun `should fail to initialize from negative value`() {
        shouldThrow<IllegalArgumentException> { (-1).points }
            .message shouldBe "Score must not be negative"
    }

    @Test
    fun `should sort by value`() {
        listOf(40.points, 1.points, 20.points, 1.points).sorted() shouldBe
                listOf(1.points, 1.points, 20.points, 40.points)
    }

    @Test
    fun `should render as string`() {
        0.points.toString() shouldBe "0 points"
    }
}
