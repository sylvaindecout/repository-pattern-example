package fr.sdecout.repository.infra.driven.jdbc.converters

import fr.sdecout.repository.domain.core.user.City
import org.jooq.impl.AbstractConverter

class CityConverter : AbstractConverter<String, City>(String::class.java, City::class.java) {
    override fun from(databaseObject: String?): City? = databaseObject?.let { City.from(it) }
    override fun to(userObject: City?): String? = userObject?.name
}
