package com.andrewbutch.noteeverything.di

import androidx.room.Room
import com.andrewbutch.noteeverything.framework.BaseApplication
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NotesDatabase
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.AuthFirestoreService
import com.andrewbutch.noteeverything.framework.datasource.network.implementation.AuthFireStoreServiceImpl
import com.andrewbutch.noteeverything.framework.datasource.network.mapper.UserNetworkMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton


/**
 * Dependencies in this module have test fakes. See in TestModule.kt (androidTest)
 */

@ExperimentalCoroutinesApi
@FlowPreview
@Module
object ProductionModule {


    @JvmStatic
    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @JvmStatic
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @JvmStatic
    @Provides
    fun provideAuthFirestoreService(
        firebaseAuth: FirebaseAuth,
        mapper: UserNetworkMapper
    ): AuthFirestoreService = AuthFireStoreServiceImpl(firebaseAuth, mapper)

    @JvmStatic
    @Singleton
    @Provides
    fun provideNotesDb(app: BaseApplication): NotesDatabase {
        return Room
            .databaseBuilder(app, NotesDatabase::class.java, NotesDatabase.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }
}