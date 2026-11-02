package com.example.devfest.domain

import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class SearchTalks @Inject constructor(private val repository: TalkRepository) {
    suspend operator fun invoke(track: String? = null, after: LocalTime? = null, query: String? = null): List<Talk> =
        repository.search(
            track?.trim()?.takeIf { it.isNotEmpty() },
            after,
            query?.trim()?.takeIf { it.isNotEmpty() },
        )
}

class GetTalk @Inject constructor(private val repository: TalkRepository) {
    suspend operator fun invoke(id: String): Talk? = repository.get(id)
}

class ObserveFavouriteIds @Inject constructor(private val repository: TalkRepository) {
    operator fun invoke(): Flow<Set<String>> = repository.observeFavouriteIds()
}

/** Result of changing the favourites. */
data class FavouritesChange(val added: Int, val total: Int)

/** Some of the given talk ids do not exist. */
class UnknownTalkException(val ids: List<String>) : IllegalArgumentException("Unknown talk ids: ${ids.joinToString()}")

class GetFavourites @Inject constructor(private val repository: TalkRepository) {
    /** Favourite talks ordered by start time. */
    suspend operator fun invoke(): List<Talk> {
        val ids = repository.favouriteIds()
        return repository.search(track = null, after = null).filter { it.id in ids }
    }
}

class AddToFavourites @Inject constructor(private val repository: TalkRepository) {
    /** Idempotent: adding a talk that is already a favourite changes nothing. Unknown ids are rejected. */
    suspend operator fun invoke(ids: Collection<String>): FavouritesChange {
        val unknown = ids.filter { repository.get(it) == null }
        if (unknown.isNotEmpty()) throw UnknownTalkException(unknown)
        val before = repository.favouriteIds()
        val after = repository.addFavourites(ids)
        return FavouritesChange(added = (after - before).size, total = after.size)
    }
}

class RemoveFavourite @Inject constructor(private val repository: TalkRepository) {
    suspend operator fun invoke(id: String) = repository.removeFavourite(id)
}

class ClearFavourites @Inject constructor(private val repository: TalkRepository) {
    suspend operator fun invoke() = repository.clearFavourites()
}
