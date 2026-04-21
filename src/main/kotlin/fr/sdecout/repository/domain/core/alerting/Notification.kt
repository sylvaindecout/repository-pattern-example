package fr.sdecout.repository.domain.core.alerting

import java.util.*

class Notification private constructor(
    val priority: PriorityLevel,
    val content: String,
) {
    companion object {
        fun of(priority: PriorityLevel, content: String) = Notification(priority, content)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Notification

        if (priority != other.priority) return false
        if (content != other.content) return false

        return true
    }

    override fun hashCode(): Int = Objects.hash(priority, content)

    override fun toString() = "[$priority] $content"
}
