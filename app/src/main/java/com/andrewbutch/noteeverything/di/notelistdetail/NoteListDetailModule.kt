package com.andrewbutch.noteeverything.di.notelistdetail

import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteListCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteListNetworkDataSource
import com.andrewbutch.noteeverything.business.interactors.common.DeleteNoteList
import com.andrewbutch.noteeverything.business.interactors.notelistdetail.NoteListDetailInteractors
import com.andrewbutch.noteeverything.business.interactors.notelistdetail.UpdateNoteList
import dagger.Module
import dagger.Provides

@Module
object NoteListDetailModule {

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
}