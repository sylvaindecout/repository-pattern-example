package fr.sdecout.repository.infra.driven.http

import fr.sdecout.repository.domain.core.alerting.Notification
import fr.sdecout.repository.domain.spi.Alerting

class HttpAlerting : Alerting {
    override fun send(message: Notification) {
        println("Pretending to send message: $message")
    }
}
