package com.andrewbutch.noteeverything.di.main

import com.andrewbutch.noteeverything.di.notedetail.NoteDetailModule
import com.andrewbutch.noteeverything.di.notedetail.NoteDetailViewModelsModule
import com.andrewbutch.noteeverything.di.notelistdetail.NoteListDetailModule
import com.andrewbutch.noteeverything.di.notelistdetail.NoteListDetailViewModelModule
import com.andrewbutch.noteeverything.di.notes.NotesViewModelsModule
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
    @FragmentScope
    @ContributesAndroidInjector(
        modules = [
            SplashViewModelModule::class,
            SplashFragmentModule::class
        ]
    )
    abstract fun contributeSplashFragment(): SplashFragment

    @FragmentScope
    @ContributesAndroidInjector(
        modules = [
            NotesViewModelsModule::class,
        ]
    )
    abstract fun contributeNotesFragment(): NotesFragment

    @FragmentScope
    @ContributesAndroidInjector(
        modules = [
            NoteDetailViewModelsModule::class,
            NoteDetailModule::class
        ]
    )
    abstract fun contributeNoteDetailFragment(): NoteDetailFragment

    @FragmentScope
    @ContributesAndroidInjector(
        modules = [
            NoteListDetailViewModelModule::class,
            NoteListDetailModule::class
        ]
    )
    abstract fun contributeNoteListDetailFragment(): NoteListDetailFragment


}