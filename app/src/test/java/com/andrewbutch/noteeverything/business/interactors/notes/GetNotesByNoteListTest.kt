package com.andrewbutch.noteeverything.business.interactors.notes

import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteCacheDataSource
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteListFactory
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.DataState
import com.andrewbutch.noteeverything.business.interactors.notes.GetNotesByNoteList.Companion.GET_NOTES_EMPTY
import com.andrewbutch.noteeverything.business.interactors.notes.GetNotesByNoteList.Companion.GET_NOTES_SUCCESS
import com.andrewbutch.noteeverything.di.DependencyContainer
import com.andrewbutch.noteeverything.framework.datasource.cache.database.ORDER_BY_DESC_DATE_CREATED
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListStateEvent.GetNotesByNoteListEvent
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListViewState
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test


/**
 * Test case
 * 1. get notes, confirm each note owner id
 *      1.1 get notes
 *      1.2 confirm message GET_NOTES_SUCCESS
 *      1.3 confirm each note "listId" field equals actual
 *      1.4 confirm notes size
 * 2. get notes by note existing owner id, confirm empty result
 *      2.1 get notes
 *      2.2 confirm message GET_NOTES_ERROR_OWNER_ID
 *      2.3 confirm empty size
 */

class GetNotesByNoteListTest {

    //test case
    private val getNotesByNoteList: GetNotesByNoteList

    // dependency
    private val dependencyContainer = DependencyContainer()
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteListFactory: NoteListFactory


    private val actualListId = "cfc3414d-5778-4abc-8a2d-d38dbc2c18ae"
    private val user = User("jLfWxedaCBdpxvcdfVpdzQIfzDw2", "", "")
    private val filterAndOrder = ORDER_BY_DESC_DATE_CREATED

    init {
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteListFactory = dependencyContainer.noteListFactory
        getNotesByNoteList = GetNotesByNoteList(noteCacheDataSource)
    }

    @Test
    fun `get notes, confirm each note owner id`() = runBlocking {
        val actualSize = 5
        // get notes
        getNotesByNoteList.getNotesByNoteList(
            stateEvent = GetNotesByNoteListEvent(
                noteListFactory.createNoteList(id = actualListId, title = ""),
                user = user
            ),
            ownerListId = actualListId,
            filterAndOrder = filterAndOrder
        )
            .collect {
                object : FlowCollector<DataState<NoteListViewState>?> {
                    override suspend fun emit(value: DataState<NoteListViewState>?) {
                        // confirm message GET_NOTES_SUCCESS
                        assertTrue(
                            "Assert success message",
                            GET_NOTES_SUCCESS == value?.stateMessage?.message
                        )

                        val notes: List<Note>? = value?.data?.notes

                        // confirm notes size
                        assertTrue("Assert size", notes?.let { it.size == actualSize } ?: false)

                        // confirm each note "listId" field equals actual
                        notes?.let {
                            for (note in notes) {
                                assertTrue("Assert owner list id", note.id == actualListId)
                            }
                        }
                    }
                }
            }
    }

    @Test
    fun `get notes by not existing owner id, confirm empty result`() = runBlocking {
        // get notes
        getNotesByNoteList.getNotesByNoteList(
            stateEvent = GetNotesByNoteListEvent(
                noteListFactory.createNoteList(id = "not exists", title = ""),
                user = user
            ),
            ownerListId = "not exists",
            filterAndOrder = filterAndOrder
        )
            .collect {
                object : FlowCollector<DataState<NoteListViewState>?> {
                    override suspend fun emit(value: DataState<NoteListViewState>?) {
                        // confirm message GET_NOTES_EMPTY
                        assertTrue(
                            "Assert empty message",
                            GET_NOTES_EMPTY == value?.stateMessage?.message
                        )

                        val notes: List<Note>? = value?.data?.notes

                        // confirm empty size
                        assertTrue("Assert size", notes?.isEmpty() ?: false)

                    }
                }
            }
    }

}