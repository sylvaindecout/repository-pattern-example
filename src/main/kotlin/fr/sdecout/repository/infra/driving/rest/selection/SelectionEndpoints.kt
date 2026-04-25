package fr.sdecout.repository.infra.driving.rest.selection

import fr.sdecout.repository.domain.api.SearchTournaments
import fr.sdecout.repository.infra.driving.rest.selection.TournamentSearchResultDto.Companion.toDto
import jakarta.validation.Valid
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Clock
import java.time.LocalDate

@RestController
@RequestMapping("/tournaments/search")
class SelectionEndpoints(
    private val searchTournaments: SearchTournaments,
    private val clock: Clock,
) {

    @PostMapping(consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun post(@Valid @RequestBody requestBody: TournamentSearchRequestDto): List<TournamentSearchResultDto> =
        searchTournaments(requestBody.toDomain(), requestedOn = { LocalDate.now(clock) }).toDto()

}
