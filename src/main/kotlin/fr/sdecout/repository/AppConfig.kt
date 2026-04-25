package fr.sdecout.repository

import fr.sdecout.repository.domain.api.*
import fr.sdecout.repository.domain.shell.*
import fr.sdecout.repository.domain.spi.*
import fr.sdecout.repository.infra.driven.http.HttpAlerting
import fr.sdecout.repository.infra.driven.jdbc.*
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
    fun playerRosters(dsl: DSLContext): PlayerRosters = DbPlayerRosters(dsl)

    @Bean
    fun scoreboardEntries(dsl: DSLContext): ScoreboardEntries = DbScoreboardEntries(dsl)

    @Bean
    fun tournamentSearchResultItems(dsl: DSLContext): TournamentSearchResultItems = DbTournamentSearchResultItems(dsl)

    @Bean
    fun alerting(): Alerting = HttpAlerting()

    /* Services */

    @Bean
    fun userAccessService(users: Users): UserAccessService = UserAccessService(users)

    @Bean
    fun userUpdateService(users: Users): UserUpdateService = UserUpdateService(users)

    @Bean
    fun tournamentAccessService(tournaments: Tournaments, tournamentSearchResultItems: TournamentSearchResultItems): TournamentAccessService =
        TournamentAccessService(tournaments, tournamentSearchResultItems)

    @Bean
    fun tournamentUpdateService(tournaments: Tournaments): TournamentUpdateService = TournamentUpdateService(tournaments)

    @Bean
    fun playerAccessService(users: Users, tournaments: Tournaments, playerRosters: PlayerRosters, scoreboardEntries: ScoreboardEntries): PlayerAccessService =
        PlayerAccessService(users, tournaments, playerRosters, scoreboardEntries)

    @Bean
    fun playerUpdateService(users: Users, tournaments: Tournaments, playerRosters: PlayerRosters, scoreboardEntries: ScoreboardEntries, alerting: Alerting): PlayerUpdateService =
        PlayerUpdateService(users, tournaments, playerRosters, scoreboardEntries, alerting)

    /* Left adapters */

    @Bean
    fun findUser(users: Users): FindUser = userAccessService(users)

    @Bean
    fun upsertUser(users: Users): UpsertUser = userUpdateService(users)

    @Bean
    fun findTournament(tournaments: Tournaments, tournamentSearchResultItems: TournamentSearchResultItems): FindTournament =
        tournamentAccessService(tournaments, tournamentSearchResultItems)

    @Bean
    fun upsertTournament(tournaments: Tournaments): UpsertTournament = tournamentUpdateService(tournaments)

    @Bean
    fun listPlayers(users: Users, tournaments: Tournaments, playerRosters: PlayerRosters, scoreboardEntries: ScoreboardEntries): ListPlayers =
        playerAccessService(users, tournaments, playerRosters, scoreboardEntries)

    @Bean
    fun addPlayer(users: Users, tournaments: Tournaments, playerRosters: PlayerRosters, scoreboardEntries: ScoreboardEntries, alerting: Alerting): AddPlayer =
        playerUpdateService(users, tournaments, playerRosters, scoreboardEntries, alerting)

    @Bean
    fun resetPlayerRoster(users: Users, tournaments: Tournaments, playerRosters: PlayerRosters, scoreboardEntries: ScoreboardEntries, alerting: Alerting): ResetPlayerRoster =
        playerUpdateService(users, tournaments, playerRosters, scoreboardEntries, alerting)

    @Bean
    fun findPlayer(users: Users, tournaments: Tournaments, playerRosters: PlayerRosters, scoreboardEntries: ScoreboardEntries): FindPlayer =
        playerAccessService(users, tournaments, playerRosters, scoreboardEntries)

    @Bean
    fun updateScore(users: Users, tournaments: Tournaments, playerRosters: PlayerRosters, scoreboardEntries: ScoreboardEntries, alerting: Alerting): UpdateScore =
        playerUpdateService(users, tournaments, playerRosters, scoreboardEntries, alerting)

    @Bean
    fun searchTournaments(tournaments: Tournaments, tournamentSearchResultItems: TournamentSearchResultItems): SearchTournaments =
        tournamentAccessService(tournaments, tournamentSearchResultItems)

}
