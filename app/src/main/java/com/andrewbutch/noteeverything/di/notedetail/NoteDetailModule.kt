package com.andrewbutch.noteeverything.di.notedetail

import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.interactors.common.DeleteNote
import com.andrewbutch.noteeverything.business.interactors.notedetail.NoteDetailInteractors
import com.andrewbutch.noteeverything.business.interactors.notedetail.UpdateNote
import com.andrewbutch.noteeverything.di.main.FragmentScope
import dagger.Module
import dagger.Provides

@Module
object NoteDetailModule {
    @FragmentScope
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

}