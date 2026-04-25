package fr.sdecout.repository.domain.core.search

import fr.sdecout.repository.domain.core.tournament.RosterSize
import fr.sdecout.repository.domain.core.tournament.RosterSize.Companion.players
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.tournament.TournamentName
import fr.sdecout.repository.domain.core.user.Age

data class TournamentSearchResultItem(
    val id: TournamentId,
    val name: TournamentName,
    val maxPlayerRosterSize: RosterSize,
    val playerRostersSize: RosterSize,
    val minimumAge: Age?,
    val averageAge: Age?,
) {
    init {
        check(playerRostersSize <= maxPlayerRosterSize) {
            "Size of the player roster (${playerRostersSize}) exceeds the limit for tournament $id ($maxPlayerRosterSize)"
        }
        check(playerRostersSize == 0.players || averageAge != null) {
            "Average age is mandatory for player roster size greater than 0 ($playerRostersSize)"
        }
        check(averageAge == null || minimumAge == null || averageAge >= minimumAge) {
            "Average age ($averageAge) is lower than the minimum age ($minimumAge)"
        }
    }

    val isFull: Boolean get() = playerRostersSize >= maxPlayerRosterSize
}
