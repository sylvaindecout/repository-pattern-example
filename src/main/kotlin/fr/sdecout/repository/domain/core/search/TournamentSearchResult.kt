package fr.sdecout.repository.domain.core.search

@JvmInline
value class TournamentSearchResult private constructor(val items: List<TournamentSearchResultItem>) {
    companion object {
        fun of(items: List<TournamentSearchResultItem>) = TournamentSearchResult(items)
        fun of(vararg items: TournamentSearchResultItem) = of(items.toList())
    }
}
