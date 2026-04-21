package fr.sdecout.repository.infra.driving.rest.roster

import fr.sdecout.repository.domain.api.AddPlayer
import fr.sdecout.repository.domain.api.ListPlayers
import fr.sdecout.repository.domain.api.ResetPlayerRoster
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.shell.DomainExceptions
import fr.sdecout.repository.infra.driving.rest.roster.PlayerDto.Companion.toDto
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.*
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponseException
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.Clock
import java.time.LocalDate

@RestController
@RequestMapping("/tournaments/{tournamentId}/players")
class RosterEndpoints(
    private val listPlayers: ListPlayers,
    private val addPlayer: AddPlayer,
    private val resetPlayerRoster: ResetPlayerRoster,
    private val clock: Clock,
) {

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun get(@PathVariable tournamentId: String): ResponseEntity<List<PlayerDto>> =
        listPlayers(TournamentId.from(tournamentId), requestedOn = { LocalDate.now(clock) })
            .map { it.toDto() }
            .let { ResponseEntity.ok(it) }

    @PostMapping(consumes = [APPLICATION_JSON_VALUE])
    fun post(
        @PathVariable tournamentId: String,
        @Valid @RequestBody requestBody: PlayerCreationRequestDto,
    ): ResponseEntity<Void> =
        addPlayer(TournamentId.from(tournamentId), requestBody.toDomain(), addedOn = { LocalDate.now(clock) })
            .let { player -> URI.create("/tournaments/$tournamentId/players/${player.userId.value}") }
            .let { location -> ResponseEntity.created(location).build() }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    fun delete(@PathVariable tournamentId: String) {
        resetPlayerRoster(TournamentId.from(tournamentId))
    }

    @ExceptionHandler(DomainExceptions.TournamentNotFound::class)
    @ResponseStatus(NOT_FOUND)
    fun handleTournamentNotFound(ex: DomainExceptions.TournamentNotFound) = ErrorResponseException(NOT_FOUND, ex)

    @ExceptionHandler(DomainExceptions.UserNotFound::class)
    @ResponseStatus(NOT_FOUND)
    fun handleUserNotFound(ex: DomainExceptions.UserNotFound) = ErrorResponseException(NOT_FOUND, ex)

    @ExceptionHandler(DomainExceptions.FullPlayerRoster::class)
    @ResponseStatus(CONFLICT)
    fun handleFullPlayerRoster(ex: DomainExceptions.FullPlayerRoster) = ErrorResponseException(CONFLICT, ex)

    @ExceptionHandler(DomainExceptions.DuplicatePlayer::class)
    @ResponseStatus(CONFLICT)
    fun handleDuplicatePlayer(ex: DomainExceptions.DuplicatePlayer) = ErrorResponseException(CONFLICT, ex)

    @ExceptionHandler(DomainExceptions.BreakingAgeLimit::class)
    @ResponseStatus(CONFLICT)
    fun handleBreakingAgeLimit(ex: DomainExceptions.BreakingAgeLimit) = ErrorResponseException(CONFLICT, ex)

}
