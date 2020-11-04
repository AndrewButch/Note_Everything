package com.andrewbutch.noteeverything.di

import android.app.Application
import com.andrewbutch.noteeverything.framework.BaseApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityBuildersModule::class,
        AppModule::class,
        ProductionModule::class,
        ViewModelFactoryModule::class
    ]
)
interface AppComponent : AndroidInjector<BaseApplication>{

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}