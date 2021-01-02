package com.andrewbutch.noteeverything.di.main

import com.andrewbutch.noteeverything.business.domain.state.MessageStack
import com.andrewbutch.noteeverything.business.domain.state.StateEventStore
import dagger.Module
import dagger.Provides

@Module
object MainModule {

    @JvmStatic
    @MainScope
    @Provides
    fun provideMessageStack(): MessageStack = MessageStack()

    @JvmStatic
    @MainScope
    @Provides
    fun provideStateEventStore(): StateEventStore = StateEventStore()
}