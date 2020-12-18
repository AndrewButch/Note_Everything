package com.andrewbutch.noteeverything.di.auth

import com.andrewbutch.noteeverything.business.data.cache.abstraction.UserCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.UserNetworkDataSource
import com.andrewbutch.noteeverything.business.data.network.implementation.UserNetworkDataSourceImpl
import com.andrewbutch.noteeverything.business.interactors.auth.AuthInteractors
import com.andrewbutch.noteeverything.business.interactors.auth.Login
import com.andrewbutch.noteeverything.business.interactors.auth.Registration
import com.andrewbutch.noteeverything.business.interactors.session.PreviousSession
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.AuthFirestoreService
import dagger.Module
import dagger.Provides

@Module
object AuthModule {

    @AuthScope
    @JvmStatic
    @Provides
    fun provideUserNetworkDataSource(authFirestoreService: AuthFirestoreService): UserNetworkDataSource {
        return UserNetworkDataSourceImpl(authFirestoreService)
    }

    @AuthScope
    @JvmStatic
    @Provides
    fun provideAuthInteractors(
        userNetworkDataSource: UserNetworkDataSource,
        userCacheDataSource: UserCacheDataSource
    ): AuthInteractors {
        return AuthInteractors(
            login = Login(userNetworkDataSource),
            registration = Registration(userNetworkDataSource),
            previousSession = PreviousSession(userCacheDataSource)
        )
    }
}