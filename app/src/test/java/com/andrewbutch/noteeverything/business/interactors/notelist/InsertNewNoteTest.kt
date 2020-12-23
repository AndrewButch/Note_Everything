package com.andrewbutch.noteeverything.business.interactors.notelist

import com.andrewbutch.noteeverything.business.data.cache.CacheConstants
import com.andrewbutch.noteeverything.business.data.cache.FakeNoteCacheDataSourceImpl
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.domain.model.NoteFactory
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.DataState
import com.andrewbutch.noteeverything.business.interactors.notelist.InsertNewNote.Companion.INSERT_NOTE_FAILED
import com.andrewbutch.noteeverything.business.interactors.notelist.InsertNewNote.Companion.INSERT_NOTE_SUCCESS
import com.andrewbutch.noteeverything.di.DependencyContainer
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListStateEvent.InsertNewNoteEvent
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListViewState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.util.*

@InternalCoroutinesApi
class InsertNewNoteTest {

    // system in test
    private val insertNewNote: InsertNewNote

    // dependency
    private val dependencyContainer = DependencyContainer()
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteNetworkDataSource: NoteNetworkDataSource
    private val noteFactory: NoteFactory

    private val user = User("jLfWxedaCBdpxvcdfVpdzQIfzDw2", "", "")

    init {
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteNetworkDataSource = dependencyContainer.noteNetworkDataSource
        noteFactory = dependencyContainer.noteFactory
        insertNewNote = InsertNewNote(noteCacheDataSource, noteNetworkDataSource, noteFactory)
    }

    /**
     * Test cases
     * 1) Insert note success, confirm network and cache update
     * 2) Insert note failure, confirm network and cache NOT update
     * 3) Insert note exception, confirm network and cache NOT update
     */

    @Test
    fun `insert note success, confirm network and cache update`() = runBlocking {
        val newNote = noteFactory.createNote(
            id = null,
            title = UUID.randomUUID().toString(),
            checked = false,
            color = null,
            listId = ""
        )

        insertNewNote.insertNote(
            id = newNote.id,
            title = newNote.title,
            color = newNote.color,
            ownerListId = newNote.listId,
            stateEvent = InsertNewNoteEvent(
                newNote.title,
                newNote.completed,
                newNote.color,
                newNote.listId,
                user
            ),
            user = user
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(INSERT_NOTE_SUCCESS, value?.stateMessage?.message)
            }
        })

        // confirm network was updated
        val insertedCacheNote = noteCacheDataSource.searchNoteById(newNote.id)
        assertTrue(insertedCacheNote == newNote)

        // confirm cache was updated
        val insertedNetworkNote = noteNetworkDataSource.searchNote(newNote, user)
        assertTrue(insertedNetworkNote == newNote)
    }

    @Test
    fun `insert note failure, confirm network and cache NOT update`() = runBlocking {
        val newNote = noteFactory.createNote(
            id = FakeNoteCacheDataSourceImpl.FORCE_FAILURE,
            title = UUID.randomUUID().toString(),
            checked = false,
            color = null,
            listId = ""
        )

        insertNewNote.insertNote(
            id = newNote.id,
            title = newNote.title,
            color = newNote.color,
            ownerListId = newNote.listId,
            stateEvent = InsertNewNoteEvent(
                newNote.title,
                newNote.completed,
                newNote.color,
                newNote.listId,
                user
            ),
            user = user
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(INSERT_NOTE_FAILED, value?.stateMessage?.message)
            }
        })

        // confirm network was not updated
        val insertedCacheNote = noteCacheDataSource.searchNoteById(newNote.id)
        assertNull(insertedCacheNote)

        // confirm cache was not updated
        val insertedNetworkNote = noteNetworkDataSource.searchNote(newNote, user)
        assertNull(insertedNetworkNote)
    }

    @Test
    fun `insert note exception, confirm network and cache NOT update`() = runBlocking {
        val newNote = noteFactory.createNote(
            id = FakeNoteCacheDataSourceImpl.FORCE_INSERT_NOTE_EXCEPTION,
            title = UUID.randomUUID().toString(),
            checked = false,
            color = null,
            listId = ""
        )

        insertNewNote.insertNote(
            id = newNote.id,
            title = newNote.title,
            color = newNote.color,
            ownerListId = newNote.listId,
            stateEvent = InsertNewNoteEvent(
                newNote.title,
                newNote.completed,
                newNote.color,
                newNote.listId,
                user
            ),
            user = user
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assert(
                    value?.stateMessage?.message
                        ?.contains(CacheConstants.CACHE_ERROR_UNKNOWN)
                        ?: false
                )
            }
        })

        // confirm network was not updated
        val insertedCacheNote = noteCacheDataSource.searchNoteById(newNote.id)
        assertNull(insertedCacheNote)

        // confirm cache was not updated
        val insertedNetworkNote = noteNetworkDataSource.searchNote(newNote, user)
        assertNull(insertedNetworkNote)
    }

}