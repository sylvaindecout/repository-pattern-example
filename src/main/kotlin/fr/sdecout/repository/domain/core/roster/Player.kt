package fr.sdecout.repository.domain.core.roster

import fr.sdecout.repository.domain.core.user.Nickname
import fr.sdecout.repository.domain.core.user.UserId

data class Player(
    val userId: UserId,
    val nickname: Nickname,
)
