package com.andrewbutch.noteeverything.di

import android.content.Context
import android.content.SharedPreferences
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteCacheDataSource
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteListCacheDataSource
import com.andrewbutch.noteeverything.business.data.cache.abstraction.UserCacheDataSource
import com.andrewbutch.noteeverything.business.data.cache.implementation.NoteCacheDataSourceImpl
import com.andrewbutch.noteeverything.business.data.cache.implementation.NoteListCacheDataSourceImpl
import com.andrewbutch.noteeverything.business.data.cache.implementation.UserCacheDataSourceImpl
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteListNetworkDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.data.network.implementation.NoteListNetworkDataSourceImpl
import com.andrewbutch.noteeverything.business.data.network.implementation.NoteNetworkDatasourceImpl
import com.andrewbutch.noteeverything.business.domain.model.NoteFactory
import com.andrewbutch.noteeverything.business.domain.model.NoteListFactory
import com.andrewbutch.noteeverything.business.domain.state.MessageStack
import com.andrewbutch.noteeverything.business.domain.state.StateEventStore
import com.andrewbutch.noteeverything.business.domain.util.DateUtil
import com.andrewbutch.noteeverything.business.interactors.common.DeleteNote
import com.andrewbutch.noteeverything.business.interactors.common.DeleteNoteList
import com.andrewbutch.noteeverything.business.interactors.notedetail.UpdateNote
import com.andrewbutch.noteeverything.business.interactors.notelist.*
import com.andrewbutch.noteeverything.framework.BaseApplication
import com.andrewbutch.noteeverything.framework.datasource.NoteDataFactory
import com.andrewbutch.noteeverything.framework.datasource.cache.abstraction.NoteDaoService
import com.andrewbutch.noteeverything.framework.datasource.cache.abstraction.NoteListDaoService
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NoteDao
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NoteListDao
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NotesDatabase
import com.andrewbutch.noteeverything.framework.datasource.cache.implementation.NoteDaoServiceImpl
import com.andrewbutch.noteeverything.framework.datasource.cache.implementation.NoteListDaoServiceImpl
import com.andrewbutch.noteeverything.framework.datasource.cache.mapper.NoteCacheMapper
import com.andrewbutch.noteeverything.framework.datasource.cache.mapper.NoteListCacheMapper
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.AuthFirestoreService
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.NoteFirestoreService
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.NoteListFirestoreService
import com.andrewbutch.noteeverything.framework.datasource.network.implementation.NoteFirestoreServiceImpl
import com.andrewbutch.noteeverything.framework.datasource.network.implementation.NoteListFirestoreServiceImpl
import com.andrewbutch.noteeverything.framework.datasource.network.mapper.NoteListNetworkMapper
import com.andrewbutch.noteeverything.framework.datasource.network.mapper.NoteNetworkMapper
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListViewState
import com.andrewbutch.noteeverything.util.PreferenceKeys
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Module
object AppModule {

    // https://developer.android.com/reference/java/text/SimpleDateFormat.html?hl=pt-br
    @JvmStatic
    @Singleton
    @Provides
    fun provideDateFormat(): SimpleDateFormat {
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH)
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

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDataFactory(
        application: BaseApplication,
    ): NoteDataFactory {
        return NoteDataFactory(application)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNotesInteractors(
        noteListCacheDataSource: NoteListCacheDataSource,
        noteListNetworkDataSource: NoteListNetworkDataSource,
        noteCacheDataSource: NoteCacheDataSource,
        noteNetworkDataSource: NoteNetworkDataSource,
        noteFactory: NoteFactory,
        noteListFactory: NoteListFactory
    ): NotesInteractors =
        NotesInteractors(
            GetAllNoteLists(noteListCacheDataSource),
            GetNotesByNoteList(noteCacheDataSource),
            DeleteMultipleNotes(noteCacheDataSource, noteNetworkDataSource),
            ClearCacheData(noteListCacheDataSource),
            InsertNewNote(noteCacheDataSource, noteNetworkDataSource, noteFactory),
            InsertNewNoteList(noteListCacheDataSource, noteListNetworkDataSource, noteListFactory),
            DeleteNote(noteCacheDataSource, noteNetworkDataSource),
            DeleteNoteList(noteListCacheDataSource, noteListNetworkDataSource),
            UpdateNote<NoteListViewState>(noteCacheDataSource, noteNetworkDataSource)
        )

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteListCacheDataSource(
        noteListDaoService: NoteListDaoService
    ): NoteListCacheDataSource =
        NoteListCacheDataSourceImpl(noteListDaoService)

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteListDaoService(
        noteListDao: NoteListDao,
        mapper: NoteListCacheMapper,
        dateUtil: DateUtil
    ): NoteListDaoService =
        NoteListDaoServiceImpl(noteListDao, mapper, dateUtil)

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteListNetworkDataSource(
        noteListFirestoreService: NoteListFirestoreService
    ): NoteListNetworkDataSource =
        NoteListNetworkDataSourceImpl(noteListFirestoreService)

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteCacheDataSource(
        noteDaoService: NoteDaoService
    ): NoteCacheDataSource =
        NoteCacheDataSourceImpl(noteDaoService)

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDaoService(
        noteDao: NoteDao,
        mapper: NoteCacheMapper,
        dateUtil: DateUtil
    ): NoteDaoService =
        NoteDaoServiceImpl(noteDao, mapper, dateUtil)

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteNetworkDataSource(
        noteFirestoreService: NoteFirestoreService
    ): NoteNetworkDataSource =
        NoteNetworkDatasourceImpl(noteFirestoreService)

    @JvmStatic
    @Singleton
    @Provides
    fun provideMessageStack(): MessageStack = MessageStack()

    @JvmStatic
    @Singleton
    @Provides
    fun provideStateEventStore(): StateEventStore = StateEventStore()

    @JvmStatic
    @Singleton
    @Provides
    fun provideSharedPreferences(app: BaseApplication): SharedPreferences {
        return app.getSharedPreferences(PreferenceKeys.APP_PREFERENCE, Context.MODE_PRIVATE)
    }

    @Singleton
    @JvmStatic
    @Provides
    fun provideUserCacheDataSource(authFirestoreService: AuthFirestoreService): UserCacheDataSource {
        return UserCacheDataSourceImpl(authFirestoreService)
    }
}