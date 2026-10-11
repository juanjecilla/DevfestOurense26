package com.example.devfest.domain

import java.time.LocalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeTalkRepository(private val talks: List<Talk>) : TalkRepository {
    private val favourites = MutableStateFlow<Set<String>>(emptySet())

    override suspend fun search(track: String?, after: LocalTime?): List<Talk> =
        talks.filter { track == null || it.track.equals(track, true) }
            .filter { after == null || !it.start.isBefore(after) }
            .sortedBy { it.start }

    override suspend fun get(id: String): Talk? = talks.firstOrNull { it.id == id }
    override fun observeFavouriteIds(): Flow<Set<String>> = favourites
    override suspend fun favouriteIds(): Set<String> = favourites.value
    override suspend fun addFavourites(ids: Collection<String>): Set<String> {
        favourites.value = favourites.value + ids
        return favourites.value
    }
    override suspend fun removeFavourite(id: String) { favourites.value = favourites.value - id }
    override suspend fun clearFavourites() { favourites.value = emptySet() }
}
