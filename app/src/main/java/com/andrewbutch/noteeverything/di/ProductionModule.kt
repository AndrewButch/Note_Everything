package com.andrewbutch.noteeverything.di

import androidx.room.Room
import com.andrewbutch.noteeverything.framework.BaseApplication
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NotesDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

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