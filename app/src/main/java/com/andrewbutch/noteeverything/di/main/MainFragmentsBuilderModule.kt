package com.andrewbutch.noteeverything.di.main

import com.andrewbutch.noteeverything.di.notedetail.NoteDetailModule
import com.andrewbutch.noteeverything.di.notelistdetail.NoteListDetailModule
import com.andrewbutch.noteeverything.di.sync.SyncFragmentModule
import com.andrewbutch.noteeverything.framework.ui.notedetail.NoteDetailFragment
import com.andrewbutch.noteeverything.framework.ui.notelistdetail.NoteListDetailFragment
import com.andrewbutch.noteeverything.framework.ui.notes.NotesFragment
import com.andrewbutch.noteeverything.framework.ui.sync.SyncFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentsBuilderModule {
    @FragmentScope
    @ContributesAndroidInjector(
        modules = [
            SyncFragmentModule::class
        ]
    )
    abstract fun contributeSplashFragment(): SyncFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeNotesFragment(): NotesFragment

    @FragmentScope
    @ContributesAndroidInjector(
        modules = [
            NoteDetailModule::class
        ]
    )
    abstract fun contributeNoteDetailFragment(): NoteDetailFragment

    @FragmentScope
    @ContributesAndroidInjector(
        modules = [
            NoteListDetailModule::class
        ]
    )
    abstract fun contributeNoteListDetailFragment(): NoteListDetailFragment


}