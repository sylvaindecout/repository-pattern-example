package fr.sdecout.repository.infra.driving.rest.user

import fr.sdecout.repository.domain.api.FindUser
import fr.sdecout.repository.domain.api.UpsertUser
import fr.sdecout.repository.domain.core.user.UserId
import fr.sdecout.repository.infra.driving.rest.user.UserDto.Companion.toDto
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserEndpoints(
    private val findUser: FindUser,
    private val upsertUser: UpsertUser,
) {

    @GetMapping("/{userId}", produces = [APPLICATION_JSON_VALUE])
    fun get(@PathVariable userId: String): ResponseEntity<UserDto> =
        findUser(UserId.from(userId))
            ?.toDto()?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PutMapping("/{userId}", consumes = [APPLICATION_JSON_VALUE])
    @ResponseStatus(NO_CONTENT)
    fun put(@PathVariable userId: String, @Valid @RequestBody requestBody: UserDto) {
        upsertUser(requestBody.toDomain())
    }

}
