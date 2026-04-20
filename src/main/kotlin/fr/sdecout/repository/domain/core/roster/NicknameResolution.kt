package fr.sdecout.repository.domain.core.roster

import fr.sdecout.repository.domain.core.user.Nickname

internal fun PlayerRoster.availableNicknameClosestTo(preferredNickname: Nickname): Nickname =
    availableNicknameClosestTo(preferredNickname, null)

private fun PlayerRoster.availableNicknameClosestTo(preferredNickname: Nickname, index: Int?): Nickname =
    preferredNickname.suffixedWith(index).let { nickname ->
        if (players.none { it.nickname == nickname }) nickname
        else availableNicknameClosestTo(preferredNickname, index = if (index == null) 1 else index + 1)
    }

private fun Nickname.suffixedWith(index: Int?) = this + (index?.let { "-$index" } ?: "")
