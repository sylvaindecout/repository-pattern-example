package fr.sdecout.repository.domain.spi

import fr.sdecout.repository.domain.core.alerting.Notification

class InMemoryNotifications : Notifications {
    private val values = mutableListOf<Notification>()

    override fun add(notification: Notification) {
        values += notification
    }

    fun findAll(): List<Notification> = values.toList()

    fun clear() = values.clear()
}
