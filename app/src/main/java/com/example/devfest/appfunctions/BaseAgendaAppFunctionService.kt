package com.example.devfest.appfunctions

import androidx.annotation.RequiresApi
import androidx.appfunctions.AppFunction
import androidx.appfunctions.AppFunctionInvalidArgumentException
import androidx.appfunctions.AppFunctionService
import androidx.appfunctions.AppFunctionServiceEntryPoint
import com.example.devfest.domain.AddToFavourites
import com.example.devfest.domain.ClearFavourites
import com.example.devfest.domain.SearchTalks
import com.example.devfest.domain.TalkRepository
import com.example.devfest.domain.Talk as DomainTalk
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RequiresApi(36)
@AndroidEntryPoint
@AppFunctionServiceEntryPoint(
    serviceName = "AgendaAppFunctionService",
    appFunctionXmlFileName = "agenda_app_function_service",
)
abstract class BaseAgendaAppFunctionService : AppFunctionService() {
    @Inject internal lateinit var searchTalksUseCase: SearchTalks
    @Inject internal lateinit var addToFavouritesUseCase: AddToFavourites
    @Inject internal lateinit var clearFavouritesUseCase: ClearFavourites
    @Inject internal lateinit var repository: TalkRepository

    /**
     * Searches talks.
     *
     * @param track Track.
     * @param after Start time.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun searchTalks(
        track: String? = null,
        after: LocalTime? = null,
    ): List<Talk> = withContext(Dispatchers.IO) {
        searchTalksUseCase(track, after).map { it.toTalk() }
    }

    /**
     * Gets favourites.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun getFavourites(): List<Talk> = withContext(Dispatchers.IO) {
        val ids = repository.favouriteIds()
        searchTalksUseCase().filter { it.id in ids }.map { it.toTalk() }
    }

    /**
     * Adds favourites.
     *
     * @param talkIds Talk ids.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun addToFavorites(talkIds: List<String>): FavouritesResult = withContext(Dispatchers.IO) {
        try {
            addToFavouritesUseCase(talkIds).let { FavouritesResult(it.added, it.total) }
        } catch (e: IllegalArgumentException) {
            throw AppFunctionInvalidArgumentException(e.message ?: "Invalid talk ids")
        }
    }

    /**
     * Clears favourites.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun clearFavorites(): FavouritesResult = withContext(Dispatchers.IO) {
        clearFavouritesUseCase()
        FavouritesResult(added = 0, total = 0)
    }

    private fun DomainTalk.toTalk() = Talk(id = id, title = title, track = track, start = start, room = room)
}
