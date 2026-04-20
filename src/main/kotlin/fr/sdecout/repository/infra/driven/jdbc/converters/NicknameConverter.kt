package fr.sdecout.repository.infra.driven.jdbc.converters

import fr.sdecout.repository.domain.core.user.Nickname
import org.jooq.impl.AbstractConverter

class NicknameConverter : AbstractConverter<String, Nickname>(String::class.java, Nickname::class.java) {
    override fun from(databaseObject: String?): Nickname? = databaseObject?.let { Nickname.from(it) }
    override fun to(userObject: Nickname?): String? = userObject?.value
}
