package fr.sdecout.repository.domain.spi

import fr.sdecout.repository.domain.core.alerting.Notification

fun interface Alerting {
    fun send(message: Notification)
}
