package com.example.devfest.domain

import java.time.LocalTime
import kotlinx.coroutines.flow.Flow

interface TalkRepository {
    /**
     * Sessions of [track] (all tracks when null) starting at or after [after] (any time when null) whose title or
     * speaker contains [query] (no text filter when null), by start time.
     */
    suspend fun search(track: String?, after: LocalTime?, query: String? = null): List<Talk>

    suspend fun get(id: String): Talk?

    fun observeFavouriteIds(): Flow<Set<String>>

    suspend fun favouriteIds(): Set<String>

    /** Adds [ids] to the favourites and returns the favourites after the change. */
    suspend fun addFavourites(ids: Collection<String>): Set<String>

    suspend fun removeFavourite(id: String)

    suspend fun clearFavourites()
}
