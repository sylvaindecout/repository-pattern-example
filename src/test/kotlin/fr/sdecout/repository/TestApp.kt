package fr.sdecout.repository

import fr.sdecout.repository.domain.TestData.today
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.testcontainers.containers.PostgreSQLContainer
import java.time.Clock
import java.time.ZoneId.systemDefault

@SpringBootApplication
class TestApp {

    @Bean
    @ServiceConnection
    fun postgreSQLContainer(): PostgreSQLContainer<*> = PostgreSQLContainer("postgres:15.17")

    @Bean
    @Primary
    fun testClock(): Clock =
        Clock.fixed(today.atStartOfDay().atZone(systemDefault()).toInstant(), systemDefault())

}
