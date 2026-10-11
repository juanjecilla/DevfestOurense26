package com.example.devfest.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.devfest.data.DefaultTalkRepository
import com.example.devfest.domain.TalkRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.favouritesDataStore: DataStore<Preferences> by preferencesDataStore(name = "favourites")

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingsModule {
    @Binds
    abstract fun bindTalkRepository(impl: DefaultTalkRepository): TalkRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideFavouritesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.favouritesDataStore
}
