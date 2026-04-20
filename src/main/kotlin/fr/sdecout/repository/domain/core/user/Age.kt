package fr.sdecout.repository.domain.core.user

import java.time.LocalDate
import java.time.Period
import kotlin.math.roundToInt

@JvmInline
value class Age private constructor(val value: Int) : Comparable<Age> {
    init {
        require(value >= 0) { "Age must not be negative" }
    }

    companion object {
        val Int.yearsOld get() = Age(this)

        fun LocalDate.toAge(computedOn: () -> LocalDate) = Period.between(this, computedOn()).years.yearsOld

        fun Collection<Age>.average(): Age? = takeUnless { it.isEmpty() }
            ?.map { it.value }
            ?.average()
            ?.roundToInt()
            ?.let { Age(it) }
    }

    override fun compareTo(other: Age): Int = this.value.compareTo(other.value)

    override fun toString() = "$value years old"
}
