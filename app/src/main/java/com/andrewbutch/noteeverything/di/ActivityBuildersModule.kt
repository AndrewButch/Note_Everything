package com.andrewbutch.noteeverything.di

import com.andrewbutch.noteeverything.di.auth.*
import com.andrewbutch.noteeverything.di.main.*
import com.andrewbutch.noteeverything.framework.ui.auth.AuthActivity
import com.andrewbutch.noteeverything.framework.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@Module
abstract class ActivityBuildersModule {

    @MainScope
    @ContributesAndroidInjector(
        modules = [
            MainModule::class,
            MainFragmentsBuilderModule::class,
            MainViewModelBuilderModule::class,
            MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity

    @AuthScope
    @ContributesAndroidInjector(
        modules = [
            AuthModule::class,
            AuthFragmentsBuilderModule::class,
            AuthViewModelModule::class,
            AuthViewModelBuilderModule::class, ]
    )
    abstract fun contributeAuthActivity(): AuthActivity
}