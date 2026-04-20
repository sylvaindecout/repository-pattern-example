package fr.sdecout.repository.infra.driven.jdbc.converters

import fr.sdecout.repository.domain.core.user.Age
import fr.sdecout.repository.domain.core.user.Age.Companion.yearsOld
import org.jooq.impl.AbstractConverter

class AgeConverter : AbstractConverter<Short, Age>(Short::class.java, Age::class.java) {
    override fun from(databaseObject: Short?): Age? = databaseObject?.toInt()?.yearsOld
    override fun to(userObject: Age?): Short? = userObject?.value?.toShort()
}
