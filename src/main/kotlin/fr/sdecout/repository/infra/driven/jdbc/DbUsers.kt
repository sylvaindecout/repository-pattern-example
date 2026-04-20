package fr.sdecout.repository.infra.driven.jdbc

import fr.sdecout.repository.domain.core.user.City
import fr.sdecout.repository.domain.core.user.Nickname
import fr.sdecout.repository.domain.core.user.User
import fr.sdecout.repository.domain.core.user.UserId
import fr.sdecout.repository.domain.spi.Users
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.records.PlayerRecord
import fr.sdecout.repository.infrastructure.driven.jdbc.jooq.tables.references.PLAYER
import org.jooq.DSLContext
import java.time.LocalDate

class DbUsers(private val dsl: DSLContext) : Users {

    override fun find(id: UserId): User? = dsl
        .selectFrom(PLAYER)
        .where(PLAYER.ID.equal(id))
        .fetchOne { it.toDomain() }

    /**
     * The responsibility to generate an ID belongs to the repository.
     *
     * Issue: If we call it twice with the same parameters, it will result in duplicates.
     *
     * Issue: This breaks the abstraction that the pattern is supposed to bring.
     * It makes the implementation more complex than it should be.
     * Some parts of the output are not provided as an input, which makes testing more complex
     * (cf. UserJourneyTest and UserEndpointsTest).
     */
    override fun create(preferredNickname: Nickname, dateOfBirth: LocalDate, city: City): User =
        User(id = UserId.generate(), preferredNickname, dateOfBirth, city).also { user ->
            dsl.insertInto(PLAYER)
                .set(user.toRecord())
                .execute()
         }

    override fun update(user: User): User = user.also { user ->
        dsl.update(PLAYER)
            .set(user.toRecord())
            .where(PLAYER.ID.equal(user.id))
            .execute()
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
