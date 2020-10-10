package com.andrewbutch.noteeverything.di

import androidx.room.Room
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NotesDatabase
import com.andrewbutch.noteeverything.framework.ui.TestBaseApplication
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Module
object TestModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideNotesDb(app: TestBaseApplication): NotesDatabase {
        return Room
            .inMemoryDatabaseBuilder(app, NotesDatabase::class.java)
            .fallbackToDestructiveMigration()
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}