package com.andrewbutch.noteeverything.framework.ui

import com.andrewbutch.noteeverything.di.DaggerTestAppComponent
import com.andrewbutch.noteeverything.framework.BaseApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class TestBaseApplication : BaseApplication() {

    override fun initAppComponent() {
        appComponent = DaggerTestAppComponent
            .factory()
            .create(this)
    }

}