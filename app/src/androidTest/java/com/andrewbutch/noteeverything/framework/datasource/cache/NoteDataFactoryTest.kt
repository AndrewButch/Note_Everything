package com.andrewbutch.noteeverything.framework.datasource.cache

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.di.TestAppComponent
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NOTE_FILTER_DATE_CREATED
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NOTE_FILTER_TITLE
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NOTE_ORDER_ASC
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NOTE_ORDER_DESC
import com.andrewbutch.noteeverything.framework.datasource.data.NoteDataFactory
import com.andrewbutch.noteeverything.framework.ui.TestBaseApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


/**
 * This is class for manual testing of data factory
 * Just check result of factory sorting
 */

@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4ClassRunner::class)
class NoteDataFactoryTest {

    private val application: TestBaseApplication =
        ApplicationProvider.getApplicationContext<Context>() as TestBaseApplication

    // System under test
    @Inject
    lateinit var dataFactory: NoteDataFactory


    init {
        (application.appComponent as TestAppComponent)
            .inject(this)

    }

    @Test
    fun getNotesSortedByTitleDesc(): Unit = runBlocking {
        val notes = dataFactory.produceSortedListOfNotesByOwnerList(
            ownerListId = "cfc3414d-5778-4abc-8a2d-d38dbc2c18ae",
            filter = NOTE_FILTER_TITLE,
            order = NOTE_ORDER_DESC
        )
        Log.d("!@#NoteDataFactoryTest: getNotesSortedByTitleDesc", prettyStringForPrint(notes))
    }

    @Test
    fun getNotesSortedByTitleAsc(): Unit = runBlocking {
        val notes = dataFactory.produceSortedListOfNotesByOwnerList(
            ownerListId = "cfc3414d-5778-4abc-8a2d-d38dbc2c18ae",
            filter = NOTE_FILTER_TITLE,
            order = NOTE_ORDER_ASC
        )
        Log.d("!@#NoteDataFactoryTest: getNotesSortedByTitleAsc", prettyStringForPrint(notes))
    }

    @Test
    fun getNotesSortedByCreatedAtDesc(): Unit = runBlocking {
        val notes = dataFactory.produceSortedListOfNotesByOwnerList(
            ownerListId = "cfc3414d-5778-4abc-8a2d-d38dbc2c18ae",
            filter = NOTE_FILTER_DATE_CREATED,
            order = NOTE_ORDER_DESC
        )
        Log.d("!@#NoteDataFactoryTest: getNotesSortedByCreatedAtDesc", prettyStringForPrint(notes))
    }

    @Test
    fun getNotesSortedByCreatedAtAsc(): Unit = runBlocking {
        val notes = dataFactory.produceSortedListOfNotesByOwnerList(
            ownerListId = "cfc3414d-5778-4abc-8a2d-d38dbc2c18ae",
            filter = NOTE_FILTER_DATE_CREATED,
            order = NOTE_ORDER_ASC
        )
        Log.d("!@#NoteDataFactoryTest: getNotesSortedByCreatedAtAsc", prettyStringForPrint(notes))
    }

    private fun prettyStringForPrint(notes: List<Note>): String {
        val builder = StringBuilder()
        builder.append("-----\n")
        for(note in notes) {
            builder.append(note)
            builder.append("\n")
        }
        return builder.toString()
    }
}