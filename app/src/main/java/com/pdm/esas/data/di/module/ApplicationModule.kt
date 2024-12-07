package com.pdm.esas.data.di.module

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.esas.data.local.preferences.UserPreferences
import com.pdm.esas.data.repository.AuthRepository
import com.pdm.esas.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        userPreferences: UserPreferences
    ): AuthRepository = AuthRepository(auth, userPreferences)

    @Provides
    @Singleton
    fun provideUserRepository(
        fireStore: FirebaseFirestore,
        userPreferences: UserPreferences
    ): UserRepository = UserRepository(fireStore, userPreferences)

}