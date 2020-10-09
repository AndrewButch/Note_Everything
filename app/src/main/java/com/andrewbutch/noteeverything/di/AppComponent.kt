package com.andrewbutch.noteeverything.di

import com.andrewbutch.noteeverything.MainActivity
import com.andrewbutch.noteeverything.framework.BaseApplication
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Singleton
@Component(
    modules = [AppModule::class]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: BaseApplication): AppComponent
    }
    fun inject(mainActivity: MainActivity)
}