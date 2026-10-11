package com.example.devfest.data

import com.example.devfest.domain.Talk
import com.example.devfest.domain.TalkRepository
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class DefaultTalkRepository @Inject constructor(
    private val favourites: FavouritesStore,
) : TalkRepository {

    override suspend fun search(track: String?, after: LocalTime?): List<Talk> =
        TalkCatalog.talks
            .filter { track == null || it.track.equals(track, ignoreCase = true) }
            .filter { after == null || !it.start.isBefore(after) }
            .sortedBy { it.start }

    override suspend fun get(id: String): Talk? = TalkCatalog.talks.firstOrNull { it.id == id }

    override fun observeFavouriteIds(): Flow<Set<String>> = favourites.observe()

    override suspend fun favouriteIds(): Set<String> = favourites.current()

    override suspend fun addFavourites(ids: Collection<String>): Set<String> = favourites.add(ids)

    override suspend fun removeFavourite(id: String) = favourites.remove(id)

    override suspend fun clearFavourites() = favourites.clear()
}
