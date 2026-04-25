package fr.sdecout.repository.domain.core.scoreboard

@JvmInline
value class Score private constructor(val value: Int) : Comparable<Score> {
    init {
        require(value >= 0) { "Score must not be negative" }
    }

    companion object {
        val Int.points get() = Score(this)
    }

    override fun compareTo(other: Score): Int = this.value.compareTo(other.value)

    override fun toString() = "$value points"
}
