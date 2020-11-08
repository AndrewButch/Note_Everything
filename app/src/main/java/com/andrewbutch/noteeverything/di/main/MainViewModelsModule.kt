package com.andrewbutch.noteeverything.di.main

import androidx.lifecycle.ViewModel
import com.andrewbutch.noteeverything.di.ViewModelKey
import com.andrewbutch.noteeverything.framework.ui.notes.NotesViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(NotesViewModel::class)
    abstract fun providesNotesViewModel(viewModel: NotesViewModel): ViewModel
}