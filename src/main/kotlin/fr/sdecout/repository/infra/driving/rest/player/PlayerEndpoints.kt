package fr.sdecout.repository.infra.driving.rest.player

import fr.sdecout.repository.domain.api.FindPlayer
import fr.sdecout.repository.domain.api.UpdateScore
import fr.sdecout.repository.domain.core.scoreboard.Score.Companion.points
import fr.sdecout.repository.domain.core.tournament.TournamentId
import fr.sdecout.repository.domain.core.user.UserId
import fr.sdecout.repository.infra.driving.rest.player.PlayerDto.Companion.toDto
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Clock
import java.time.LocalDate

@RestController
@RequestMapping("/tournaments/{tournamentId}/players/{playerId}")
class PlayerEndpoints(
    private val findPlayer: FindPlayer,
    private val updateScore: UpdateScore,
    private val clock: Clock,
) {

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun get(@PathVariable tournamentId: String, @PathVariable playerId: String): ResponseEntity<PlayerDto> =
        findPlayer(TournamentId.from(tournamentId), UserId.from(playerId), { LocalDate.now(clock) })
            ?.toDto()?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PutMapping("/score", consumes = [APPLICATION_JSON_VALUE])
    @ResponseStatus(NO_CONTENT)
    fun put(
        @PathVariable tournamentId: String,
        @PathVariable playerId: String,
        @Valid @RequestBody requestBody: ScoreUpdateRequestDto,
    ) {
        updateScore(TournamentId.from(tournamentId), UserId.from(playerId), requestBody.score.points)
    }

}
