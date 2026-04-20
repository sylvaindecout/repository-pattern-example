package fr.sdecout.repository.infra.driven.jdbc.converters

import fr.sdecout.repository.domain.core.tournament.TournamentName
import org.jooq.impl.AbstractConverter

class TournamentNameConverter
    : AbstractConverter<String, TournamentName>(String::class.java, TournamentName::class.java) {
    override fun from(databaseObject: String?): TournamentName? = databaseObject?.let { TournamentName.from(it) }
    override fun to(userObject: TournamentName?): String? = userObject?.value
}
