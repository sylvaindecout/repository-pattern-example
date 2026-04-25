package fr.sdecout.repository.infra.driven.jdbc.functions

import org.jooq.Field
import org.jooq.impl.DSL
import org.jooq.types.YearToSecond
import java.time.LocalDate

object PostgresqlFunctions {

    fun age(now: Field<LocalDate?>, dateOfBirth: Field<LocalDate?>): Field<YearToSecond> =
        DSL.field("AGE({0}, {1})", YearToSecond::class.java, now, dateOfBirth)

}
