package fr.sdecout.repository.infra.driven.http

import fr.sdecout.repository.domain.core.alerting.Notification
import fr.sdecout.repository.domain.spi.Notifications

class HttpAlerting : Notifications {
    override fun add(notification: Notification) {
        println("Pretending to send message: $notification")
    }
}
