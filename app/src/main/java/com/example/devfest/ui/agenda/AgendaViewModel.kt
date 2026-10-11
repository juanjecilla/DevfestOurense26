package com.example.devfest.ui.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devfest.data.TalkCatalog
import com.example.devfest.domain.AddToFavourites
import com.example.devfest.domain.ClearFavourites
import com.example.devfest.domain.ObserveFavouriteIds
import com.example.devfest.domain.RemoveFavourite
import com.example.devfest.domain.SearchTalks
import com.example.devfest.domain.Talk
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AgendaState(
    val tracks: List<String> = TalkCatalog.tracks,
    val selectedTrack: String? = null,
    val talks: List<Talk> = emptyList(),
    val favouriteIds: Set<String> = emptySet(),
) {
    val favourites: List<Talk> get() = talks.filter { it.id in favouriteIds }
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val searchTalks: SearchTalks,
    observeFavouriteIds: ObserveFavouriteIds,
    private val addToFavourites: AddToFavourites,
    private val removeFavourite: RemoveFavourite,
    private val clearFavourites: ClearFavourites,
) : ViewModel() {

    private val selectedTrack = MutableStateFlow<String?>(null)

    /** Favourites always show the whole agenda, whatever the track filter says. */
    val state: StateFlow<AgendaState> = combine(
        selectedTrack,
        selectedTrack.flatMapLatest { track -> flow { emit(searchTalks(track)) } },
        observeFavouriteIds(),
    ) { track, talks, favourites ->
        AgendaState(selectedTrack = track, talks = talks, favouriteIds = favourites)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AgendaState())

    fun selectTrack(track: String?) {
        selectedTrack.value = track
    }

    fun toggleFavourite(talk: Talk) {
        viewModelScope.launch {
            if (talk.id in state.value.favouriteIds) removeFavourite(talk.id) else addToFavourites(listOf(talk.id))
        }
    }

    fun clearAll() {
        viewModelScope.launch { clearFavourites() }
    }
}
