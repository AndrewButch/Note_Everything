package com.andrewbutch.noteeverything.di.notelistdetail

import androidx.lifecycle.ViewModel
import com.andrewbutch.noteeverything.di.ViewModelKey
import com.andrewbutch.noteeverything.framework.ui.notelistdetail.NoteListDetailViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class NoteListDetailViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(NoteListDetailViewModel::class)
    abstract fun providesNoteListDetailViewModel(viewModel: NoteListDetailViewModel): ViewModel
}