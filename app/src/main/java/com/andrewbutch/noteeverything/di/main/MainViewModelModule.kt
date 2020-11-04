package com.andrewbutch.noteeverything.di.main

import androidx.lifecycle.ViewModel
import com.andrewbutch.noteeverything.di.ViewModelKey
import com.andrewbutch.noteeverything.framework.ui.notes.NotesViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(value = NotesViewModel::class)
    abstract fun bindNotesViewModel(notesViewModel: NotesViewModel) : ViewModel
}