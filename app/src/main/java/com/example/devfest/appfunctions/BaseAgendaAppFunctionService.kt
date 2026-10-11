package com.example.devfest.appfunctions

import androidx.annotation.RequiresApi
import androidx.appfunctions.AppFunction
import androidx.appfunctions.AppFunctionElementNotFoundException
import androidx.appfunctions.AppFunctionInvalidArgumentException
import androidx.appfunctions.AppFunctionLimitExceededException
import androidx.appfunctions.AppFunctionService
import androidx.appfunctions.AppFunctionServiceEntryPoint
import com.example.devfest.domain.AddToFavourites
import com.example.devfest.domain.ClearFavourites
import com.example.devfest.domain.GetFavourites
import com.example.devfest.domain.SearchTalks
import com.example.devfest.domain.UnknownTalkException
import com.example.devfest.domain.Talk as DomainTalk
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime
import java.time.format.DateTimeParseException
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
    @Inject internal lateinit var getFavouritesUseCase: GetFavourites

    /**
     * Search the DevFest Ourense agenda by track and start time.
     * Required workflow: Call this before "addToFavorites" to obtain valid talk IDs.
     *
     * @param track Track: "android", "web", "cloud", "ai" or "general". Null searches all.
     * @param after Earliest start time as HH:mm, local to the event, e.g. "14:00". Null: whole day.
     * @return Talks ordered by start time. Empty list when nothing matches.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun searchTalks(
        track: String? = null,
        after: String? = null,
    ): List<Talk> = withContext(Dispatchers.IO) {
        val from = try {
            after?.let(LocalTime::parse)
        } catch (e: DateTimeParseException) {
            throw AppFunctionInvalidArgumentException("after must be HH:mm, for example 14:00")
        }
        searchTalksUseCase(track, from).map { it.toTalk() }
    }

    /**
     * List the talks the user saved as favourites, ordered by start time.
     * Use it to report what is saved or to check overlaps with other commitments.
     *
     * @return Favourite talks. Empty list when none is saved.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun getFavourites(): List<Talk> = withContext(Dispatchers.IO) {
        getFavouritesUseCase().map { it.toTalk() }
    }

    /**
     * Save talks to the user's favourites. Adding a talk that is already saved changes nothing.
     * Required workflow: Call "searchTalks" first to obtain valid talk IDs.
     *
     * @param talkIds IDs returned by "searchTalks", at most 20 per call.
     *   If an ID is unknown the call fails and nothing is saved.
     * @return Number of talks newly added and total favourites after the call.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun addToFavorites(talkIds: List<String>): FavouritesResult = withContext(Dispatchers.IO) {
        if (talkIds.isEmpty()) throw AppFunctionInvalidArgumentException("talkIds must not be empty")
        if (talkIds.size > MAX_IDS) throw AppFunctionLimitExceededException("At most $MAX_IDS ids per call")
        try {
            addToFavouritesUseCase(talkIds).let { FavouritesResult(it.added, it.total) }
        } catch (e: UnknownTalkException) {
            throw AppFunctionElementNotFoundException("Talk not found for ID = ${e.ids.joinToString()}")
        }
    }

    /**
     * Remove every favourite talk. Irreversible.
     * Requires confirmation: ask the user to confirm, then call with confirmed = true.
     *
     * @param confirmed True only after the user explicitly agreed to clear all favourites.
     * @return Result with zero favourites.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun clearFavorites(confirmed: Boolean): FavouritesResult = withContext(Dispatchers.IO) {
        if (!confirmed) {
            throw AppFunctionInvalidArgumentException(
                "Not confirmed. Ask the user, then call again with confirmed = true.",
            )
        }
        clearFavouritesUseCase()
        FavouritesResult(added = 0, total = 0)
    }

    private companion object {
        const val MAX_IDS = 20
    }

    private fun DomainTalk.toTalk() = Talk(id = id, title = title, track = track, start = start.toString(), room = room)
}
