package fr.sdecout.repository.domain.core.tournament

@JvmInline
value class RosterSize private constructor(val value: Int) : Comparable<RosterSize> {
    init {
        require(value >= 0) { "Roster size must not be negative" }
    }

    companion object {
        val Int.players get() = RosterSize(this)
    }

    override fun compareTo(other: RosterSize): Int = this.value.compareTo(other.value)

    override fun toString() = "$value players"
}
