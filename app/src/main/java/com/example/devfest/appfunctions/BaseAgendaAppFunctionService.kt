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
     * Search the DevFest Ourense agenda by track and start time.
     * Required workflow: Call this before "addToFavorites" to obtain valid talk IDs.
     *
     * @param track Track name: "android", "web", "cloud", "ai" or "general". Case-insensitive. If null, searches all tracks.
     * @param after Earliest start time, local to the event, for example 14:00. If null, returns the whole day.
     * @return Talks ordered by start time. Empty list when nothing matches.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun searchTalks(
        track: String? = null,
        after: LocalTime? = null,
    ): List<Talk> = withContext(Dispatchers.IO) {
        searchTalksUseCase(track, after).map { it.toTalk() }
    }

    /**
     * List the talks the user saved as favourites, ordered by start time.
     * Use it to report what is saved or to check overlaps with the user's other commitments.
     *
     * @return Favourite talks. Empty list when none is saved.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun getFavourites(): List<Talk> = withContext(Dispatchers.IO) {
        val ids = repository.favouriteIds()
        searchTalksUseCase().filter { it.id in ids }.map { it.toTalk() }
    }

    /**
     * Save talks to the user's favourites. Adding a talk that is already saved changes nothing.
     * Required workflow: Call "searchTalks" first to obtain valid talk IDs.
     *
     * @param talkIds Talk IDs returned by "searchTalks". If an ID is unknown the call fails and nothing is saved.
     * @return Number of talks newly added and total favourites after the call.
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
     * Remove every favourite talk. Irreversible.
     * Requires confirmation: ask the user to confirm before calling this function.
     *
     * @return Result with zero favourites.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun clearFavorites(): FavouritesResult = withContext(Dispatchers.IO) {
        clearFavouritesUseCase()
        FavouritesResult(added = 0, total = 0)
    }

    private fun DomainTalk.toTalk() = Talk(id = id, title = title, track = track, start = start, room = room)
}
