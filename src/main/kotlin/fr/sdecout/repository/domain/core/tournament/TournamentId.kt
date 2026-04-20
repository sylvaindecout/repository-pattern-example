package fr.sdecout.repository.domain.core.tournament

@JvmInline
value class TournamentId private constructor(val value: String) {
    init {
        require(value.none { it.isWhitespace() }) { "Tournament ID must not contain white spaces" }
    }

    companion object {
        fun from(value: String) = TournamentId(value)
    }

    override fun toString() = value
}
