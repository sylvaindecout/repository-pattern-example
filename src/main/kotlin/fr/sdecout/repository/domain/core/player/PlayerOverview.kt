package fr.sdecout.repository.domain.core.player

import fr.sdecout.repository.domain.core.roster.Player
import fr.sdecout.repository.domain.core.user.Age
import fr.sdecout.repository.domain.core.user.City
import fr.sdecout.repository.domain.core.user.Nickname
import fr.sdecout.repository.domain.core.user.UserId

data class PlayerOverview(
    val userId: UserId,
    val nickname: Nickname,
    val age: Age,
    val city: City,
) {
    companion object {
        fun Player.with(age: Age, city: City) = PlayerOverview(userId, nickname, age, city)
    }

    val player: Player get() = Player(userId, nickname)
}
