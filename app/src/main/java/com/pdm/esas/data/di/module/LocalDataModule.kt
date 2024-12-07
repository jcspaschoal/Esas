package com.pdm.esas.data.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "esas-prefs")


@Module
@InstallIn(SingletonComponent::class)
class LocalDataModule {

    @Provides
    @Singleton
    fun provideDataStorePreferences(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.dataStore

}