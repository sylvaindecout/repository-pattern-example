package fr.sdecout.repository

import fr.sdecout.repository.domain.api.FindUser
import fr.sdecout.repository.domain.api.UpsertUser
import fr.sdecout.repository.domain.shell.UserAccessService
import fr.sdecout.repository.domain.shell.UserUpdateService
import fr.sdecout.repository.domain.spi.Users
import fr.sdecout.repository.infra.driven.jdbc.DbUsers
import org.jooq.DSLContext
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

    /* Right adapters */

    @Bean
    fun users(dsl: DSLContext): Users = DbUsers(dsl)

    /* Services */

    @Bean
    fun userAccessService(users: Users) : UserAccessService = UserAccessService(users)

    @Bean
    fun userUpdateService(users: Users) : UserUpdateService = UserUpdateService(users)

    /* Left adapters */

    @Bean
    fun findUser(users: Users): FindUser = userAccessService(users)

    @Bean
    fun upsertUser(users: Users): UpsertUser = userUpdateService(users)

}
