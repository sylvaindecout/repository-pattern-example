package fr.sdecout.repository.domain.core.user

@JvmInline
value class City private constructor(val name: String) {
    init {
        require(name.length > 1) { "City name must have at least 2 characters" }
    }

    companion object {
        fun from(name: String) = City(name)
    }

    override fun toString() = name
}
