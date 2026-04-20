package fr.sdecout.repository.domain.core.user

@JvmInline
value class Nickname private constructor(val value: String) {
    companion object {
        fun from(value: String) = Nickname(value.trim().lowercase())
    }

    init {
        require(value.isNotBlank()) { "Nickname must not be blank" }
    }

    operator fun plus(suffix: String) = Nickname(value + suffix)

    override fun toString() = value
}
