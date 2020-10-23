package com.andrewbutch.noteeverything.di

import com.andrewbutch.noteeverything.di.main.MainFragmentBuildersModule
import com.andrewbutch.noteeverything.framework.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@Module
abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(
        modules = [MainFragmentBuildersModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity

    // TODO contribute auth activity
}