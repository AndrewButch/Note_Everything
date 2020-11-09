package com.andrewbutch.noteeverything.di.splash

import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteCacheDataSource
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteListCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteListNetworkDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.interactors.splash.SyncNoteLists
import com.andrewbutch.noteeverything.business.interactors.splash.SyncNotes
import com.andrewbutch.noteeverything.framework.ui.splash.SyncManager
import dagger.Module
import dagger.Provides

@Module
object SplashFragmentModule {

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

