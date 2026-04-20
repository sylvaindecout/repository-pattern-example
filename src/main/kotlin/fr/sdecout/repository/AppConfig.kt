package fr.sdecout.repository

import fr.sdecout.repository.domain.api.FindTournament
import fr.sdecout.repository.domain.api.FindUser
import fr.sdecout.repository.domain.api.UpsertTournament
import fr.sdecout.repository.domain.api.UpsertUser
import fr.sdecout.repository.domain.shell.TournamentAccessService
import fr.sdecout.repository.domain.shell.TournamentUpdateService
import fr.sdecout.repository.domain.shell.UserAccessService
import fr.sdecout.repository.domain.shell.UserUpdateService
import fr.sdecout.repository.domain.spi.Tournaments
import fr.sdecout.repository.domain.spi.Users
import fr.sdecout.repository.infra.driven.jdbc.DbTournaments
import fr.sdecout.repository.infra.driven.jdbc.DbUsers
import fr.sdecout.repository.infra.driven.jdbc.TournamentRepository
import fr.sdecout.repository.infra.driven.jdbc.TournamentRepositoryImpl
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

    @Bean
    fun tournaments(tournamentRepository: TournamentRepository): Tournaments = DbTournaments(tournamentRepository)

    @Bean
    fun tournamentRepository(dsl: DSLContext): TournamentRepository = TournamentRepositoryImpl(dsl)

    /* Services */

    @Bean
    fun userAccessService(users: Users): UserAccessService = UserAccessService(users)

    @Bean
    fun userUpdateService(users: Users): UserUpdateService = UserUpdateService(users)

    @Bean
    fun tournamentAccessService(tournaments: Tournaments): TournamentAccessService =
        TournamentAccessService(tournaments)

    @Bean
    fun tournamentUpdateService(tournaments: Tournaments): TournamentUpdateService =
        TournamentUpdateService(tournaments)

    /* Left adapters */

    @Bean
    fun findUser(users: Users): FindUser = userAccessService(users)

    @Bean
    fun upsertUser(users: Users): UpsertUser = userUpdateService(users)

    @Bean
    fun findTournament(tournaments: Tournaments): FindTournament = tournamentAccessService(tournaments)

    @Bean
    fun upsertTournament(tournaments: Tournaments): UpsertTournament = tournamentUpdateService(tournaments)

}
