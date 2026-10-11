package com.example.devfest.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/** Favourite talk ids, persisted so they survive the process being killed. */
class FavouritesStore @Inject constructor(private val dataStore: DataStore<Preferences>) {
    private val key = stringSetPreferencesKey("favourite_ids")

    fun observe(): Flow<Set<String>> = dataStore.data.map { it[key] ?: emptySet() }

    suspend fun current(): Set<String> = observe().first()

    suspend fun add(ids: Collection<String>): Set<String> =
        dataStore.edit { it[key] = (it[key] ?: emptySet()) + ids }[key] ?: emptySet()

    suspend fun remove(id: String) {
        dataStore.edit { it[key] = (it[key] ?: emptySet()) - id }
    }

    suspend fun clear() {
        dataStore.edit { it.remove(key) }
    }
}
