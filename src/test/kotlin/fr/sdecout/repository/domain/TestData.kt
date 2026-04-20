package fr.sdecout.repository.domain

import fr.sdecout.repository.domain.TestData.Cities.naples
import fr.sdecout.repository.domain.TestData.Cities.nyc
import fr.sdecout.repository.domain.TestData.Cities.orlando
import fr.sdecout.repository.domain.TestData.Cities.tokyo
import fr.sdecout.repository.domain.core.user.City
import fr.sdecout.repository.domain.core.user.Nickname
import fr.sdecout.repository.domain.core.user.User
import fr.sdecout.repository.domain.core.user.UserId
import java.time.LocalDate

object TestData {
    val today: LocalDate = LocalDate.parse("1998-03-01")

    object Cities {
        val naples get() = City.from("naples")
        val tokyo get() = City.from("tokyo")
        val orlando get() = City.from("orlando")
        val nyc get() = City.from("new york city")
    }

    object Users {
        private fun User.Companion.from(id: String, preferredNickname: String, dateOfBirth: String, city: City) = User(
            id = UserId.from(id),
            preferredNickname = Nickname.from(preferredNickname),
            dateOfBirth = LocalDate.parse(dateOfBirth),
            city = city,
        )

        val giorno get() = User.from("019cc9df-0a49-7fe8-9265-fd00996bd267", "giorno", "1985-04-16", naples)
        val jotaro get() = User.from("019cc9df-a40f-7dcf-9ed5-ab5d27ab9ff1", "jotaro", "1971-11-01", tokyo)
        val jolyne get() = User.from("019cc9df-eb7f-7432-a6d0-0fa4fe72e3e4", "jolyne", "1992-02-25", orlando)
        val joseph get() = User.from("019cc9e0-0234-75c1-8c50-8ec99b7ab16e", "joseph", "1920-09-27", nyc)
    }

}
