package fr.sdecout.repository.infra.driven.jdbc.converters

import fr.sdecout.repository.domain.core.scoreboard.Score
import fr.sdecout.repository.domain.core.scoreboard.Score.Companion.points
import org.jooq.impl.AbstractConverter

class ScoreConverter : AbstractConverter<Short, Score>(Short::class.java, Score::class.java) {
    override fun from(databaseObject: Short?): Score? = databaseObject?.toInt()?.points
    override fun to(userObject: Score?): Short? = userObject?.value?.toShort()
}
