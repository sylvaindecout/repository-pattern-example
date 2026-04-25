package fr.sdecout.repository.infra.driving.rest.selection

import fr.sdecout.repository.domain.core.search.TournamentSearchResult
import fr.sdecout.repository.domain.core.search.TournamentSearchResultItem
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class TournamentSearchResultDto(
    @field:NotBlank val id: String,
    @field:NotBlank val name: String,
    @field:Positive val maxPlayerRosterSize: Int,
    @field:Positive val minimumAge: Int?,
    @field:Positive val playerRosterSize: Int,
    @field:Positive val averageAge: Int?,
) {
    companion object {
        fun TournamentSearchResult.toDto() = items.map { it.toDto() }

        private fun TournamentSearchResultItem.toDto() = TournamentSearchResultDto(
            id = id.value,
            name = name.value,
            maxPlayerRosterSize = maxPlayerRosterSize.value,
            minimumAge = minimumAge?.value,
            playerRosterSize = playerRostersSize.value,
            averageAge = averageAge?.value,
        )
    }
}
