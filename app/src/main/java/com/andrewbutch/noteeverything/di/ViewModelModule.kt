package com.andrewbutch.noteeverything.di

import androidx.lifecycle.ViewModel
import com.andrewbutch.noteeverything.framework.ui.auth.AuthViewModel
import com.andrewbutch.noteeverything.framework.ui.notedetail.NoteDetailViewModel
import com.andrewbutch.noteeverything.framework.ui.notelistdetail.NoteListDetailViewModel
import com.andrewbutch.noteeverything.framework.ui.notes.NotesViewModel
import com.andrewbutch.noteeverything.framework.ui.splash.SplashViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
abstract class ViewModelModule {

    @Singleton
    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun providesAuthViewModel(viewModel: AuthViewModel): ViewModel

    @Singleton
    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun providesSplashViewModel(viewModel: SplashViewModel): ViewModel

    @Singleton
    @Binds
    @IntoMap
    @ViewModelKey(NotesViewModel::class)
    abstract fun providesNotesViewModel(viewModel: NotesViewModel): ViewModel

    @Singleton
    @Binds
    @IntoMap
    @ViewModelKey(NoteListDetailViewModel::class)
    abstract fun providesNoteListDetailViewModel(viewModel: NoteListDetailViewModel): ViewModel

    @Singleton
    @Binds
    @IntoMap
    @ViewModelKey(NoteDetailViewModel::class)
    abstract fun providesNoteDetailViewModel(viewModel: NoteDetailViewModel): ViewModel


}