package com.andrewbutch.noteeverything.di

import com.andrewbutch.noteeverything.di.auth.AuthFragmentsBuilderModule
import com.andrewbutch.noteeverything.di.auth.AuthModule
import com.andrewbutch.noteeverything.di.main.MainFragmentsBuilderModule
import com.andrewbutch.noteeverything.di.scope.AuthScope
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

    @ContributesAndroidInjector(
        modules = [MainFragmentsBuilderModule::class,]
    )
    abstract fun contributeMainActivity(): MainActivity

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentsBuilderModule::class ]
    )
    abstract fun contributeAuthActivity(): AuthActivity
}