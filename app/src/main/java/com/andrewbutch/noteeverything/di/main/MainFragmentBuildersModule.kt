package com.andrewbutch.noteeverything.di.main

import com.andrewbutch.noteeverything.framework.ui.notes.NotesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeNotesFragment(): NotesFragment
}