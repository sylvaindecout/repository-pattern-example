package fr.sdecout.repository.domain.core.player

import fr.sdecout.repository.domain.core.roster.Player
import fr.sdecout.repository.domain.core.scoreboard.Score
import fr.sdecout.repository.domain.core.user.Age
import fr.sdecout.repository.domain.core.user.City
import fr.sdecout.repository.domain.core.user.Nickname
import fr.sdecout.repository.domain.core.user.UserId

data class PlayerOverview(
    val userId: UserId,
    val nickname: Nickname,
    val age: Age,
    val city: City,
    val score: Score,
) {
    companion object {
        fun Player.with(age: Age, city: City, score: Score) = PlayerOverview(userId, nickname, age, city, score)
    }

    val player: Player get() = Player(userId, nickname)
}
