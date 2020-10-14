package com.andrewbutch.noteeverything.di

import androidx.room.Room
import com.andrewbutch.noteeverything.business.domain.model.NoteFactory
import com.andrewbutch.noteeverything.business.domain.model.NoteListFactory
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NotesDatabase
import com.andrewbutch.noteeverything.framework.datasource.data.NoteDataFactory
import com.andrewbutch.noteeverything.framework.ui.TestBaseApplication
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

//    @JvmStatic
//    @Singleton
//    @Provides
//    fun provideFirebaseFirestore(): FirebaseFirestore {
//        return FirebaseFirestore.getInstance()
//    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDataFactory(
        application: TestBaseApplication,
        noteFactory: NoteFactory,
        noteListFactory: NoteListFactory
    ): NoteDataFactory {
        return NoteDataFactory(application, noteFactory, noteListFactory)
    }
}