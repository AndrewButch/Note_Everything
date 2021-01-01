package com.andrewbutch.noteeverything.di.main

import androidx.lifecycle.ViewModel
import com.andrewbutch.noteeverything.di.ViewModelKey
import com.andrewbutch.noteeverything.framework.ui.notedetail.NoteDetailViewModel
import com.andrewbutch.noteeverything.framework.ui.notelistdetail.NoteListDetailViewModel
import com.andrewbutch.noteeverything.framework.ui.notes.NotesViewModel
import com.andrewbutch.noteeverything.framework.ui.sync.SyncViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @MainScope
    @Binds
    @IntoMap
    @ViewModelKey(SyncViewModel::class)
    abstract fun providesSplashViewModel(viewModel: SyncViewModel): ViewModel

    @MainScope
    @Binds
    @IntoMap
    @ViewModelKey(NotesViewModel::class)
    abstract fun providesNotesViewModel(viewModel: NotesViewModel): ViewModel

    @MainScope
    @Binds
    @IntoMap
    @ViewModelKey(NoteListDetailViewModel::class)
    abstract fun providesNoteListDetailViewModel(viewModel: NoteListDetailViewModel): ViewModel

    @MainScope
    @Binds
    @IntoMap
    @ViewModelKey(NoteDetailViewModel::class)
    abstract fun providesNoteDetailViewModel(viewModel: NoteDetailViewModel): ViewModel
}