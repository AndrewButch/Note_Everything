package com.andrewbutch.noteeverything.di

import com.andrewbutch.noteeverything.business.domain.util.DateUtil
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NoteDao
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NoteListDao
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NotesDatabase
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.NoteFirestoreService
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.NoteListFirestoreService
import com.andrewbutch.noteeverything.framework.datasource.network.implementation.NoteFirestoreServiceImpl
import com.andrewbutch.noteeverything.framework.datasource.network.implementation.NoteListFirestoreServiceImpl
import com.andrewbutch.noteeverything.framework.datasource.network.mapper.NoteListNetworkMapper
import com.andrewbutch.noteeverything.framework.datasource.network.mapper.NoteNetworkMapper
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton

@Module
object AppModule {

    // https://developer.android.com/reference/java/text/SimpleDateFormat.html?hl=pt-br
    @JvmStatic
    @Singleton
    @Provides
    fun provideDateFormat(): SimpleDateFormat {
        val sdf = SimpleDateFormat("dd.MM.yyyy hh:mm:ss", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC+3") // match firestore
        return sdf
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideDateUtil(dateFormat: SimpleDateFormat): DateUtil {
        return DateUtil(
            dateFormat
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDao(database: NotesDatabase): NoteDao {
        return database.noteDao()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteListDao(database: NotesDatabase): NoteListDao {
        return database.noteListDao()
    }


    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteListFirestoreService(
        firestore: FirebaseFirestore,
        mapper: NoteListNetworkMapper
    ): NoteListFirestoreService {
        return NoteListFirestoreServiceImpl(firestore, mapper)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteFirestoreService(
        firestore: FirebaseFirestore,
        mapper: NoteNetworkMapper
    ): NoteFirestoreService {
        return NoteFirestoreServiceImpl(firestore, mapper)
    }


}