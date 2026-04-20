package fr.sdecout.repository

import fr.sdecout.repository.domain.api.*
import fr.sdecout.repository.domain.shell.*
import fr.sdecout.repository.domain.spi.PlayerRosterEntries
import fr.sdecout.repository.domain.spi.Tournaments
import fr.sdecout.repository.domain.spi.Users
import fr.sdecout.repository.infra.driven.jdbc.DbPlayerRosterEntries
import fr.sdecout.repository.infra.driven.jdbc.DbTournaments
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

    @Bean
    fun tournaments(dsl: DSLContext): Tournaments = DbTournaments(dsl)

    @Bean
    fun playerRosterEntries(dsl: DSLContext): PlayerRosterEntries = DbPlayerRosterEntries(dsl)

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

    @Bean
    fun playerAccessService(tournaments: Tournaments, playerRosterEntries: PlayerRosterEntries): PlayerAccessService =
        PlayerAccessService(tournaments, playerRosterEntries)

    @Bean
    fun playerUpdateService(users: Users, tournaments: Tournaments, playerRosterEntries: PlayerRosterEntries): PlayerUpdateService =
        PlayerUpdateService(users, tournaments, playerRosterEntries)

    /* Left adapters */

    @Bean
    fun findUser(users: Users): FindUser = userAccessService(users)

    @Bean
    fun upsertUser(users: Users): UpsertUser = userUpdateService(users)

    @Bean
    fun findTournament(tournaments: Tournaments): FindTournament = tournamentAccessService(tournaments)

    @Bean
    fun upsertTournament(tournaments: Tournaments): UpsertTournament = tournamentUpdateService(tournaments)

    @Bean
    fun listPlayers(users: Users, tournaments: Tournaments, playerRosterEntries: PlayerRosterEntries): ListPlayers =
        playerAccessService(tournaments, playerRosterEntries)

    @Bean
    fun addPlayer(users: Users, tournaments: Tournaments, playerRosterEntries: PlayerRosterEntries): AddPlayer =
        playerUpdateService(users, tournaments, playerRosterEntries)

    @Bean
    fun resetPlayerRoster(users: Users, tournaments: Tournaments, playerRosterEntries: PlayerRosterEntries): ResetPlayerRoster =
        playerUpdateService(users, tournaments, playerRosterEntries)

}
