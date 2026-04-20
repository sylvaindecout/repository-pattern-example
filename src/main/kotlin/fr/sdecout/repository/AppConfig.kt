package fr.sdecout.repository

import org.jooq.conf.RenderNameCase
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class AppConfig {

    @Bean
    fun configurationCustomizer() = DefaultConfigurationCustomizer {
        it.settings().withRenderNameCase(RenderNameCase.LOWER)
    }

    @Bean
    fun clock(): Clock = Clock.systemDefaultZone()

}
