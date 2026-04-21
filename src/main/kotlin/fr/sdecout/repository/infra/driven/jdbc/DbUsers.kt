package fr.sdecout.repository.infra.driven.jdbc

import fr.sdecout.repository.domain.core.user.User
import fr.sdecout.repository.domain.core.user.UserId
import fr.sdecout.repository.domain.spi.Users
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.records.PlayerRecord
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.references.PLAYER
import org.jooq.DSLContext

class DbUsers(private val dsl: DSLContext) : Users {

    override fun find(id: UserId): User? = dsl
        .selectFrom(PLAYER)
        .where(PLAYER.ID.equal(id))
        .fetchOne { it.toDomain() }

    override fun findIn(ids: Collection<UserId>): Collection<User> = dsl
        .selectFrom(PLAYER)
        .where(PLAYER.ID.`in`(ids))
        .fetch { it.toDomain() }

    override fun save(user: User) {
        user.toRecord().let { record ->
            dsl.insertInto(PLAYER)
                .set(record)
                .onDuplicateKeyUpdate()
                .set(record)
                .execute()
        }
    }

    private fun PlayerRecord.toDomain() = User(
        id = id,
        preferredNickname = preferredNickname,
        dateOfBirth = dateOfBirth,
        city = city,
    )

    private fun User.toRecord() = PLAYER.newRecord()
        .with(PLAYER.ID, id)
        .with(PLAYER.PREFERRED_NICKNAME, preferredNickname)
        .with(PLAYER.DATE_OF_BIRTH, dateOfBirth)
        .with(PLAYER.CITY, city)
}
