package com.andrewbutch.noteeverything.di.auth

import com.andrewbutch.noteeverything.di.scope.AuthScope
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.AuthFirestoreService
import com.andrewbutch.noteeverything.framework.datasource.network.implementation.AuthFireStoreServiceImpl
import com.andrewbutch.noteeverything.framework.datasource.network.mapper.UserNetworkMapper
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides

@Module
object AuthModule {

    @JvmStatic
    @AuthScope
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @JvmStatic
    @AuthScope
    @Provides
    fun provideAuthFirestoreService(
        firebaseAuth: FirebaseAuth,
        mapper: UserNetworkMapper
    ): AuthFirestoreService = AuthFireStoreServiceImpl(firebaseAuth, mapper)

}