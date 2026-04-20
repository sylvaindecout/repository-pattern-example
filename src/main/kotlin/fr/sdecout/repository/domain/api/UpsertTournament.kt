package fr.sdecout.repository.domain.api

import fr.sdecout.repository.domain.core.tournament.Tournament

fun interface UpsertTournament {
    fun upsert(tournament: Tournament)

    operator fun invoke(tournament: Tournament) = upsert(tournament)
}
