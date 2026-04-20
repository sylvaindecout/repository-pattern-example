package fr.sdecout.repository.infra.driven.jdbc.converters

import fr.sdecout.repository.domain.core.tournament.RosterSize
import fr.sdecout.repository.domain.core.tournament.RosterSize.Companion.players
import org.jooq.impl.AbstractConverter

class RosterSizeConverter : AbstractConverter<Short, RosterSize>(Short::class.java, RosterSize::class.java) {
    override fun from(databaseObject: Short?): RosterSize? = databaseObject?.toInt()?.players
    override fun to(userObject: RosterSize?): Short? = userObject?.value?.toShort()
}
