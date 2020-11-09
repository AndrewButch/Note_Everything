package com.andrewbutch.noteeverything.di.splash

import androidx.lifecycle.ViewModel
import com.andrewbutch.noteeverything.di.ViewModelKey
import com.andrewbutch.noteeverything.framework.ui.splash.SplashViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SplashViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun providesSplashViewModel(viewModel: SplashViewModel): ViewModel
}