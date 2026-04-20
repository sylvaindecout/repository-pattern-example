package fr.sdecout.repository.infra.driving.rest.tournament

import fr.sdecout.repository.domain.api.FindTournament
import fr.sdecout.repository.domain.api.UpsertTournament
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.infra.driving.rest.tournament.TournamentDto.Companion.toDto
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tournaments")
class TournamentEndpoints(
    private val findTournament: FindTournament,
    private val upsertTournament: UpsertTournament,
) {

    @GetMapping("/{tournamentId}", produces = [APPLICATION_JSON_VALUE])
    fun get(@PathVariable tournamentId: String): ResponseEntity<TournamentDto> =
        findTournament(TournamentId.from(tournamentId))
            ?.toDto()?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PutMapping("/{tournamentId}", consumes = [APPLICATION_JSON_VALUE])
    @ResponseStatus(NO_CONTENT)
    fun put(@PathVariable tournamentId: String, @Valid @RequestBody requestBody: TournamentDto) {
        upsertTournament(requestBody.toDomain())
    }

}
