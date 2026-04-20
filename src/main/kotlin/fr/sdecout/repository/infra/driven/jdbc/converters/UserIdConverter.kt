package fr.sdecout.repository.infra.driven.jdbc.converters

import fr.sdecout.repository.domain.core.user.UserId
import org.jooq.impl.AbstractConverter

class UserIdConverter : AbstractConverter<String, UserId>(String::class.java, UserId::class.java) {
    override fun from(databaseObject: String?): UserId? = databaseObject?.let { UserId.from(it) }
    override fun to(userObject: UserId?): String? = userObject?.value
}
