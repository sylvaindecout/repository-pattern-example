package fr.sdecout.repository.infra.driven.jdbc

import fr.sdecout.repository.domain.core.roster.Player
import fr.sdecout.repository.domain.core.roster.PlayerRoster
import fr.sdecout.repository.domain.core.scoreboard.Score
import fr.sdecout.repository.domain.core.scoreboard.Score.Companion.points
import fr.sdecout.repository.domain.core.tournament.RosterSize
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.Age
import fr.sdecout.repository.domain.core.user.Nickname
import fr.sdecout.repository.domain.core.user.UserId
import fr.sdecout.repository.domain.spi.PlayerRosters
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.records.RosterEntryRecord
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.references.ROSTER_ENTRY
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.references.SCOREBOARD_ENTRY
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.references.TOURNAMENT
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record3
import org.jooq.Record4
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.select
import org.jooq.impl.DSL.selectFrom

class DbPlayerRosters(private val dsl: DSLContext) : PlayerRosters {
    private typealias Row = Record4<TournamentId?, RosterSize?, Age?, List<Player>?>
    private typealias PlayerRow = Record3<UserId?, Nickname?, Score?>

    private val players: Field<List<Player>> = multiset(
        select(ROSTER_ENTRY.PLAYER, ROSTER_ENTRY.NICKNAME, SCOREBOARD_ENTRY.SCORE)
            .from(ROSTER_ENTRY)
            .leftJoin(SCOREBOARD_ENTRY).on(SCOREBOARD_ENTRY.PLAYER.equal(ROSTER_ENTRY.PLAYER)
                .and(SCOREBOARD_ENTRY.TOURNAMENT.equal(ROSTER_ENTRY.TOURNAMENT)))
            .where(ROSTER_ENTRY.TOURNAMENT.eq(TOURNAMENT.ID))
    ).`as`("players").convertFrom { it.toDomain() }

    override fun find(id: TournamentId): PlayerRoster? = dsl
        .select(
            TOURNAMENT.ID,
            TOURNAMENT.MAX_PLAYER_ROSTER_SIZE,
            TOURNAMENT.MIN_AGE,
            players,
        )
        .from(TOURNAMENT)
        .where(TOURNAMENT.ID.eq(id))
        .fetchOne { it.toDomain() }

    override fun save(playerRoster: PlayerRoster) {
        val commands = playerRoster.pendingPlayers
            .map { player -> prepareUpsert(player, playerRoster.tournamentId) }
        dsl.batch(commands).execute()
    }

    override fun remove(id: TournamentId) {
        dsl.deleteFrom(ROSTER_ENTRY)
            .where(ROSTER_ENTRY.TOURNAMENT.eq(id))
            .execute()
    }

    private fun prepareUpsert(player: Player, tournamentId: TournamentId) =
        recordFrom(player, tournamentId).prepareUpsert()

    private fun recordFrom(player: Player, tournamentId: TournamentId) = ROSTER_ENTRY.newRecord()
        .with(ROSTER_ENTRY.TOURNAMENT, tournamentId)
        .with(ROSTER_ENTRY.PLAYER, player.userId)
        .with(ROSTER_ENTRY.NICKNAME, player.nickname)

    private fun RosterEntryRecord.prepareUpsert() = dsl
        .insertInto(ROSTER_ENTRY)
        .set(this)
        .onDuplicateKeyUpdate()
        .set(this)

    private fun List<PlayerRow>.toDomain() = map { it.toDomain() }

    private fun PlayerRow.toDomain() = Player(
        userId = get(ROSTER_ENTRY.PLAYER)!!,
        nickname = get(ROSTER_ENTRY.NICKNAME)!!,
        score = get(SCOREBOARD_ENTRY.SCORE) ?: 0.points,
    )

    private fun Row.toDomain() = PlayerRoster.from(
        tournamentId = get(TOURNAMENT.ID)!!,
        maxPlayerRosterSize = get(TOURNAMENT.MAX_PLAYER_ROSTER_SIZE)!!,
        minimumAge = get(TOURNAMENT.MIN_AGE),
        players = get(players),
    )
}
