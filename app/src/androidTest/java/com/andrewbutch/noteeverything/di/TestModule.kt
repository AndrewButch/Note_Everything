package com.andrewbutch.noteeverything.di

import androidx.room.Room
import com.andrewbutch.noteeverything.business.domain.model.NoteFactory
import com.andrewbutch.noteeverything.business.domain.model.NoteListFactory
import com.andrewbutch.noteeverything.business.domain.util.DateUtil
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NotesDatabase
import com.andrewbutch.noteeverything.framework.datasource.data.NoteDataFactory
import com.andrewbutch.noteeverything.framework.ui.TestBaseApplication
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
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
    fun provideFirestoreSettings(): FirebaseFirestoreSettings {
        return FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
    }


    @JvmStatic
    @Singleton
    @Provides
    fun provideFirebaseFirestore(settings: FirebaseFirestoreSettings): FirebaseFirestore {
        // 10.0.2.2 is the special IP address to connect to the 'localhost' of
        // the host computer from an Android emulator.

        return FirebaseFirestore.getInstance().also {
            it.useEmulator("10.0.2.2", 8080)
            it.firestoreSettings = settings
        }
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDataFactory(
        application: TestBaseApplication,
        noteFactory: NoteFactory,
        noteListFactory: NoteListFactory,
        dateUtil: DateUtil
    ): NoteDataFactory {
        return NoteDataFactory(application, noteFactory, noteListFactory, dateUtil)
    }
}