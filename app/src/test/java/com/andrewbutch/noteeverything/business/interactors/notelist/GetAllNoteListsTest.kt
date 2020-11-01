package com.andrewbutch.noteeverything.business.interactors.notelist

import com.andrewbutch.noteeverything.business.data.cache.FakeNoteListCacheDataSourceImpl
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteListCacheDataSource
import com.andrewbutch.noteeverything.business.domain.state.DataState
import com.andrewbutch.noteeverything.business.interactors.notelist.GetAllNoteLists.Companion.GET_ALL_NOTE_LIST_EMPTY
import com.andrewbutch.noteeverything.business.interactors.notelist.GetAllNoteLists.Companion.GET_ALL_NOTE_LIST_SUCCESS
import com.andrewbutch.noteeverything.di.DependencyContainer
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListStateEvent
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListViewState
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


/** Test case
 * 1. get all note lists, check size
 *      1.1 get note lists
 *      1.2 confirm GET_ALL_NOTE_LIST_SUCCESS message
 *      1.3 confirm data size == actual size
 * 2. get all notes list, check empty
 *      2.1 set fake data source with empty data
 *      2.2 get note lists
 *      2.3 confirm GET_ALL_NOTE_LIST_EMPTY message
 *      2.4 confirm data is empty
 */

class GetAllNoteListsTest {

    // test object
    private lateinit var getAllNoteLists: GetAllNoteLists

    // dependency
    private var dependencyContainer = DependencyContainer()
    private lateinit var noteListCacheDataSource: NoteListCacheDataSource


    @Before
    fun before() {
        dependencyContainer.build()
        noteListCacheDataSource = dependencyContainer.noteListCacheDataSource
        getAllNoteLists = GetAllNoteLists(noteListCacheDataSource)
    }

    @Test
    fun `get all note lists, check size`() = runBlocking {
        val actualSize = 5
        // get note lists
        getAllNoteLists.getAllNoteLists(NoteListStateEvent.GetAllNoteListsEvent())
            .collect {
                object : FlowCollector<DataState<NoteListViewState>?> {
                    override suspend fun emit(value: DataState<NoteListViewState>?) {
                        // confirm GET_ALL_NOTE_LIST_SUCCESS message
                        assertTrue(
                            "Assert success message",
                            GET_ALL_NOTE_LIST_SUCCESS == value?.stateMessage?.message
                        )

                        // confirm data size == actual size
                        assertTrue(
                            "Assert size",
                            value?.data?.noteLists?.let {
                                it.size == actualSize
                            } ?: false
                        )
                    }

                }
            }
    }

    @Test
    fun `get all note lists, check empty`() = runBlocking {
        // set fake data source with empty data
        noteListCacheDataSource =
            FakeNoteListCacheDataSourceImpl(HashMap(), dependencyContainer.dateUtil)

        // get note lists
        getAllNoteLists.getAllNoteLists(NoteListStateEvent.GetAllNoteListsEvent())
            .collect {
                object : FlowCollector<DataState<NoteListViewState>?> {
                    override suspend fun emit(value: DataState<NoteListViewState>?) {

                        // confirm GET_ALL_NOTE_LIST_EMPTY message
                        assertTrue(
                            "Assert empty message",
                            GET_ALL_NOTE_LIST_EMPTY == value?.stateMessage?.message
                        )

                        // confirm data is empty
                        assertTrue(
                            "Assert empty",
                            value?.data?.noteLists?.isEmpty() ?: false
                        )
                    }
                }
            }
    }
}