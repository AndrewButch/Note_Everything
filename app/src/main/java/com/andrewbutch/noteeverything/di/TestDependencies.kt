package com.andrewbutch.noteeverything.di

import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteCacheDataSource
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteListCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteListNetworkDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.UserNetworkDataSource
import com.andrewbutch.noteeverything.business.data.network.implementation.UserNetworkDataSourceImpl
import com.andrewbutch.noteeverything.business.interactors.auth.AuthInteractors
import com.andrewbutch.noteeverything.business.interactors.auth.Login
import com.andrewbutch.noteeverything.business.interactors.common.DeleteNote
import com.andrewbutch.noteeverything.business.interactors.common.DeleteNoteList
import com.andrewbutch.noteeverything.business.interactors.notedetail.NoteDetailInteractors
import com.andrewbutch.noteeverything.business.interactors.notedetail.UpdateNote
import com.andrewbutch.noteeverything.business.interactors.notelistdetail.NoteListDetailInteractors
import com.andrewbutch.noteeverything.business.interactors.notelistdetail.UpdateNoteList
import com.andrewbutch.noteeverything.business.interactors.splash.SyncNoteLists
import com.andrewbutch.noteeverything.business.interactors.splash.SyncNotes
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.AuthFirestoreService
import com.andrewbutch.noteeverything.framework.ui.splash.SyncManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object TestDependencies {

    @Singleton
    @JvmStatic
    @Provides
    fun provideUserNetworkDataSource(authFirestoreService: AuthFirestoreService): UserNetworkDataSource {
        return UserNetworkDataSourceImpl(authFirestoreService)
    }

    @Singleton
    @JvmStatic
    @Provides
    fun provideAuthInteractors(
        userNetworkDataSource: UserNetworkDataSource
    ): AuthInteractors {
        return AuthInteractors(Login(userNetworkDataSource))
    }

    @Singleton
    @JvmStatic
    @Provides
    fun providesNoteDetailInteractor(
        noteCacheDataSource: NoteCacheDataSource,
        noteNetworkDataSource: NoteNetworkDataSource,
    ): NoteDetailInteractors {
        return NoteDetailInteractors(
            updateNote = UpdateNote(noteCacheDataSource, noteNetworkDataSource),
            deleteNote = DeleteNote(noteCacheDataSource, noteNetworkDataSource)
        )
    }

    @Singleton
    @JvmStatic
    @Provides
    fun provideNoteListDetailInteractors(
        noteListCacheDataSource: NoteListCacheDataSource,
        noteListNetworkDataSource: NoteListNetworkDataSource
    ): NoteListDetailInteractors {
        return NoteListDetailInteractors(
            updateNoteList = UpdateNoteList(noteListCacheDataSource, noteListNetworkDataSource),
            deleteNoteList = DeleteNoteList(noteListCacheDataSource, noteListNetworkDataSource)
        )
    }

    @Singleton
    @JvmStatic
    @Provides
    fun provideSyncManager(
        noteListCacheDataSource: NoteListCacheDataSource,
        noteListNetworkDataSource: NoteListNetworkDataSource,
        noteCacheDataSource: NoteCacheDataSource,
        noteNetworkDataSource: NoteNetworkDataSource,
    ): SyncManager = SyncManager(
        SyncNoteLists(noteListCacheDataSource, noteListNetworkDataSource),
        SyncNotes(noteCacheDataSource, noteNetworkDataSource)
    )
}