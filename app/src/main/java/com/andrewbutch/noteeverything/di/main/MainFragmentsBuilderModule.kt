package com.andrewbutch.noteeverything.di.main

import com.andrewbutch.noteeverything.di.splash.SplashFragmentModule
import com.andrewbutch.noteeverything.di.splash.SplashViewModelModule
import com.andrewbutch.noteeverything.framework.ui.notedetail.NoteDetailFragment
import com.andrewbutch.noteeverything.framework.ui.notelistdetail.NoteListDetailFragment
import com.andrewbutch.noteeverything.framework.ui.notes.NotesFragment
import com.andrewbutch.noteeverything.framework.ui.splash.SplashFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentsBuilderModule {

    @ContributesAndroidInjector(
        modules = [
            MainViewModelsModule::class,
        ]
    )
    abstract fun contributeNotesFragment(): NotesFragment

    @ContributesAndroidInjector
    abstract fun contributeNoteDetailFragment(): NoteDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeNoteListDetailFragment(): NoteListDetailFragment

    @ContributesAndroidInjector(
        modules = [
            SplashViewModelModule::class,
            SplashFragmentModule::class
        ]
    )
    abstract fun contributeSplashFragment(): SplashFragment
}