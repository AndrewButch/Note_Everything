package com.andrewbutch.noteeverything.di.notedetail

import androidx.lifecycle.ViewModel
import com.andrewbutch.noteeverything.di.ViewModelKey
import com.andrewbutch.noteeverything.framework.ui.notedetail.NoteDetailViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class NoteDetailViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(NoteDetailViewModel::class)
    abstract fun providesNoteDetailViewModel(viewModel: NoteDetailViewModel): ViewModel
}