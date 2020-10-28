package com.andrewbutch.noteeverything.framework.ui

import android.app.Application
import com.andrewbutch.noteeverything.di.DaggerTestAppComponent
import com.andrewbutch.noteeverything.di.TestAppComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class TestBaseApplication : Application() {

    lateinit var appComponent: TestAppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerTestAppComponent
            .factory()
            .create(this)
    }

}