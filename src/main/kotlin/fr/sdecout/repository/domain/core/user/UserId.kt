package fr.sdecout.repository.domain.core.user

import java.util.*

@JvmInline
value class UserId private constructor(val value: String) {
    init {
        require(value.none { it.isWhitespace() }) { "User ID must not contain white spaces" }
    }

    companion object {
        fun from(value: String) = UserId(value)

        fun generate() = from(UUID.randomUUID().toString())
    }

    override fun toString() = value
}
