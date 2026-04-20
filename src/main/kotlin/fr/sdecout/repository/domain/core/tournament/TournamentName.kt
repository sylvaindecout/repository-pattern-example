package fr.sdecout.repository.domain.core.tournament

@JvmInline
value class TournamentName private constructor(val value: String) {
    companion object {
        fun from(value: String) = TournamentName(value.trim())
    }

    init {
        require(value.isNotBlank()) { "Tournament name must not be blank" }
    }

    override fun toString() = value
}
