package com.pdm.esas.data.di.module

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.esas.data.local.memory.InMemoryUserInfo
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
        inMemoryUserInfo: InMemoryUserInfo
    ): AuthRepository = AuthRepository(auth, inMemoryUserInfo)

    @Provides
    @Singleton
    fun provideUserRepository(
        fireStore: FirebaseFirestore,
        inMemoryUserInfo: InMemoryUserInfo
    ): UserRepository = UserRepository(fireStore, inMemoryUserInfo)

}