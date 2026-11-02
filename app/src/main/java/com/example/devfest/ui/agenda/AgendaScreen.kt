package com.example.devfest.ui.agenda

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devfest.domain.Talk
import java.time.format.DateTimeFormatter

private val TimeFormat = DateTimeFormatter.ofPattern("HH:mm")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(viewModel: AgendaViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var tab by remember { mutableIntStateOf(0) }
    var confirmClear by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("DevFest Ourense 26") }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            PrimaryTabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Agenda") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Favourites (${state.favouriteIds.size})") })
            }
            if (tab == 0) {
                TrackFilter(state, viewModel::selectTrack)
                TalkList(state.talks, state.favouriteIds, viewModel::toggleFavourite)
            } else {
                if (state.favouriteIds.isNotEmpty()) {
                    TextButton(onClick = { confirmClear = true }, modifier = Modifier.padding(horizontal = 8.dp)) {
                        Text("Clear all favourites")
                    }
                }
                TalkList(state.favourites, state.favouriteIds, viewModel::toggleFavourite)
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Clear all favourites?") },
            text = { Text("This cannot be undone.") },
            confirmButton = { TextButton(onClick = { viewModel.clearAll(); confirmClear = false }) { Text("Clear") } },
            dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun TrackFilter(state: AgendaState, onSelect: (String?) -> Unit) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item { FilterChip(selected = state.selectedTrack == null, onClick = { onSelect(null) }, label = { Text("All") }) }
        items(state.tracks) { track ->
            FilterChip(selected = state.selectedTrack == track, onClick = { onSelect(track) }, label = { Text(track) })
        }
    }
}

@Composable
private fun TalkList(talks: List<Talk>, favouriteIds: Set<String>, onToggle: (Talk) -> Unit) {
    LazyColumn(Modifier.fillMaxWidth()) {
        items(talks, key = { it.id }) { talk ->
            ListItem(
                modifier = Modifier.clickable { onToggle(talk) },
                overlineContent = { Text("${talk.start.format(TimeFormat)} · ${talk.room} · ${talk.track}") },
                headlineContent = { Text(talk.title) },
                supportingContent = talk.speaker?.let { speaker -> { Text(speaker) } },
                trailingContent = {
                    Text(
                        if (talk.id in favouriteIds) "★" else "☆",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
            )
        }
    }
}
