package fr.sdecout.repository.infra.driven.jdbc.converters

import fr.sdecout.repository.domain.core.tournament.TournamentId
import org.jooq.impl.AbstractConverter

class TournamentIdConverter : AbstractConverter<String, TournamentId>(String::class.java, TournamentId::class.java) {
    override fun from(databaseObject: String?): TournamentId? = databaseObject?.let { TournamentId.from(it) }
    override fun to(userObject: TournamentId?): String? = userObject?.value
}
