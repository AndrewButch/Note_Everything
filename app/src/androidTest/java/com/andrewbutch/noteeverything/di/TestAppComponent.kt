package com.andrewbutch.noteeverything.di

import com.andrewbutch.noteeverything.framework.datasource.cache.NoteDaoServiceTest
import com.andrewbutch.noteeverything.framework.datasource.cache.NoteListDaoServiceTest
import com.andrewbutch.noteeverything.framework.datasource.network.NoteListFirestoreServiceTest
import com.andrewbutch.noteeverything.framework.ui.TestBaseApplication
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton


@ExperimentalCoroutinesApi
@FlowPreview
@Singleton
@Component(
    modules = [
        TestModule::class,
        AppModule::class
    ]
)
interface TestAppComponent : AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: TestBaseApplication): TestAppComponent
    }

    fun inject(noteListDaoServiceTest: NoteListDaoServiceTest)
    fun inject(noteDaoServiceTest: NoteDaoServiceTest)
    fun inject(noteListFirestoreServiceTest: NoteListFirestoreServiceTest)
}