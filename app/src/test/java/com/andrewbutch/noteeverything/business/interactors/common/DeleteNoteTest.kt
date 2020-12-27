package com.andrewbutch.noteeverything.business.interactors.common

import com.andrewbutch.noteeverything.business.data.cache.CacheConstants
import com.andrewbutch.noteeverything.business.data.cache.FakeNoteCacheDataSourceImpl
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.domain.model.NoteFactory
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.DataState
import com.andrewbutch.noteeverything.di.DependencyContainer
import com.andrewbutch.noteeverything.framework.datasource.cache.database.ORDER_BY_DESC_DATE_UPDATED
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListStateEvent
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListViewState
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DeleteNoteTest {

    // test object
    private val deleteNote: DeleteNote<NoteListViewState>

    // dependency
    private val dependencyContainer = DependencyContainer()
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteNetworkDataSource: NoteNetworkDataSource
    private val noteFactory: NoteFactory

    private val ownerListId = "cfc3414d-5778-4abc-8a2d-d38dbc2c18ae"
    private val user = User("jLfWxedaCBdpxvcdfVpdzQIfzDw2", "", "")
    private val filterAndOrder = ORDER_BY_DESC_DATE_UPDATED

    init {
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteNetworkDataSource = dependencyContainer.noteNetworkDataSource
        noteFactory = dependencyContainer.noteFactory
        deleteNote = DeleteNote(noteCacheDataSource, noteNetworkDataSource)
    }

    /**
     * Test cases
     * 1) Delete success (return 1), confirm delete from cache and network
     * 2) Delete failure (return -1), confirm NOT delete from cache and network
     * 3) Delete exception, confirm NOT delete from cache and network
     */
    @Test
    fun `Delete success (return 1), confirm delete from cache and network`() = runBlocking {
        val randomNote =
            noteCacheDataSource
                .getNotesByOwnerListId(ownerListId, filterAndOrder)
                .shuffled()
                .first()

        deleteNote.deleteNote(
            note = randomNote,
            stateEvent = NoteListStateEvent.DeleteNoteEvent(randomNote, user),
            user = user
        ).collect {
            object : FlowCollector<DataState<NoteListViewState>?> {
                override suspend fun emit(value: DataState<NoteListViewState>?) {
                    assertTrue(
                        "Assert delete message",
                        DeleteNote.DELETE_NOTE_SUCCESS == value?.stateMessage?.message
                    )
                }
            }
        }

        // confirm cache delete note
        val deletedCache = noteCacheDataSource.searchNoteById(randomNote.id)
        assertNull("Assert delete from cache", deletedCache)

        // confirm network delete note
        val deletedNetwork = noteNetworkDataSource.searchNote(randomNote, user)
        assertNull("Assert delete from network", deletedNetwork)
    }

    @Test
    fun `Delete failure (return -1), confirm NOT delete from cache and network`() = runBlocking {
        val noteToDelete = noteFactory.createNote(title = "", listId = "")
        val cacheSizeBefore =
            noteCacheDataSource
                .getNotesByOwnerListId(ownerListId, filterAndOrder)
                .size
        val networkSizeBefore = noteNetworkDataSource.getNotesByOwnerListId(ownerListId, user).size


        deleteNote.deleteNote(
            note = noteToDelete,
            stateEvent = NoteListStateEvent.DeleteNoteEvent(noteToDelete, user),
            user = user
        ).collect {
            object : FlowCollector<DataState<NoteListViewState>?> {
                override suspend fun emit(value: DataState<NoteListViewState>?) {
                    assertTrue(
                        "Assert delete message",
                        DeleteNote.DELETE_NOTE_FAILED == value?.stateMessage?.message
                    )
                }
            }
        }

        // confirm cache unchanged
        val cacheSizeAfter = noteCacheDataSource
            .getNotesByOwnerListId(ownerListId, filterAndOrder)
            .size
        assertTrue("Assert not delete from cache", cacheSizeBefore == cacheSizeAfter)

        // confirm network unchanged
        val networkSizeAfter = noteNetworkDataSource.getNotesByOwnerListId(ownerListId, user).size
        assertTrue("Assert not delete from network", networkSizeBefore == networkSizeAfter)
    }

    @Test
    fun `Delete exception, confirm NOT delete from cache and network`() = runBlocking {
        val noteToDelete = noteFactory.createNote(
            id = FakeNoteCacheDataSourceImpl.FORCE_DELETE_NOTE_EXCEPTION,
            title = "",
            listId = ""
        )
        val cacheSizeBefore = noteCacheDataSource
            .getNotesByOwnerListId(ownerListId, filterAndOrder)
            .size
        val networkSizeBefore = noteNetworkDataSource.getNotesByOwnerListId("", user).size


        deleteNote.deleteNote(
            note = noteToDelete,
            stateEvent = NoteListStateEvent.DeleteNoteEvent(noteToDelete, user),
            user = user
        ).collect {
            object : FlowCollector<DataState<NoteListViewState>?> {
                override suspend fun emit(value: DataState<NoteListViewState>?) {
                    assert(
                        value?.stateMessage?.message
                            ?.contains(CacheConstants.CACHE_ERROR_UNKNOWN)
                            ?: false
                    )
                }
            }
        }

        // confirm cache unchanged
        val cacheSizeAfter = noteCacheDataSource
            .getNotesByOwnerListId(ownerListId, filterAndOrder)
            .size
        assertTrue("Assert not delete from cache", cacheSizeBefore == cacheSizeAfter)

        // confirm network unchanged
        val networkSizeAfter = noteNetworkDataSource.getNotesByOwnerListId("", user).size
        assertTrue("Assert not delete from network", networkSizeBefore == networkSizeAfter)
    }
}