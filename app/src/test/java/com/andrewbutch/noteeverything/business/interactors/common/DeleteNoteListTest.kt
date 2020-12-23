package com.andrewbutch.noteeverything.business.interactors.common

import com.andrewbutch.noteeverything.business.data.cache.CacheConstants
import com.andrewbutch.noteeverything.business.data.cache.FakeNoteListCacheDataSourceImpl
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteListCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteListNetworkDataSource
import com.andrewbutch.noteeverything.business.domain.model.NoteListFactory
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.DataState
import com.andrewbutch.noteeverything.di.DependencyContainer
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListStateEvent
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListViewState
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test


/**
 * Test cases
 * 1. Delete success (return 1), confirm deleted from cache and network
 *      1.1 Get random note list from test data
 *      1.2 Delete
 *      1.3 Confirm deleted from cache
 *      1.4 Confirm deleted from network
 * 2. Delete failure (return -1), confirm NOT deleted from cache and network
 *      2.1 Save cache and network size
 *      2.2 Create note with not existing id
 *      2.3 Delete
 *      2.4 Confirm cache size unchanged
 *      2.5 Confirm network size unchanged
 * 3. Delete exception, confirm NOT deleted from cache and network
 *      3.1 Save cache and network size
 *      3.2 Create note with id = FORCE_DELETE_NOTE_LIST_EXCEPTION
 *      3.3 Delete
 *      3.4 Confirm cache size unchanged
 *      3.5 Confirm network size unchanged
 */

class DeleteNoteListTest {

    // test object
    private val deleteNoteList: DeleteNoteList<NoteListViewState>

    // dependency
    private val dependencyContainer = DependencyContainer()
    private val noteListCacheDataSource: NoteListCacheDataSource
    private val noteListNetworkDataSource: NoteListNetworkDataSource
    private val noteListFactory: NoteListFactory

    private val user = User("jLfWxedaCBdpxvcdfVpdzQIfzDw2", "", "")


    init {
        dependencyContainer.build()
        noteListCacheDataSource = dependencyContainer.noteListCacheDataSource
        noteListNetworkDataSource = dependencyContainer.noteListNetworkDataSource
        noteListFactory = dependencyContainer.noteListFactory
        deleteNoteList = DeleteNoteList(noteListCacheDataSource, noteListNetworkDataSource)
    }

    @Test
    fun `Delete success (return 1), confirm deleted from cache and network`() = runBlocking {
        // Get random note list from test data
        val randomNoteList = noteListCacheDataSource.getAllNoteLists().shuffled().first()
        // Delete
        deleteNoteList.deleteNoteList(
            noteList = randomNoteList,
            stateEvent = NoteListStateEvent.DeleteNoteListEvent(randomNoteList, user),
            user = user
        )
            .collect {
                object : FlowCollector<DataState<NoteListStateEvent>?> {
                    override suspend fun emit(value: DataState<NoteListStateEvent>?) {
                        assertTrue(
                            "Assert delete note list success",
                            DeleteNoteList.DELETE_NOTE_LIST_SUCCESS == value?.stateMessage?.message
                        )
                    }


                }
            }
        // Confirm deleted from cache
        assertFalse(noteListCacheDataSource.getAllNoteLists().contains(randomNoteList))
        // Confirm deleted from network
        assertFalse(noteListNetworkDataSource.getAllNoteLists(user).contains(randomNoteList))
    }

    @Test
    fun `Delete failure (return -1), confirm NOT deleted from cache and network`() = runBlocking {
        // Save cache and network size
        val prevCacheSize = noteListCacheDataSource.getAllNoteLists().size
        val prevNetworkSize = noteListNetworkDataSource.getAllNoteLists(user).size
        // Create note with not existing i
        val noteListToDelete = noteListFactory.createNoteList(title = "")
        // Delete
        deleteNoteList.deleteNoteList(
            noteList = noteListToDelete,
            stateEvent = NoteListStateEvent.DeleteNoteListEvent(noteListToDelete, user),
            user = user
        ).collect {
            object : FlowCollector<DataState<NoteListViewState>?> {
                override suspend fun emit(value: DataState<NoteListViewState>?) {
                    assertTrue(
                        "Assert delete note list failed",
                        DeleteNoteList.DELETE_NOTE_LIST_FAILED == value?.stateMessage?.message
                    )
                }

            }
        }
        // Confirm cache size unchanged
        assertTrue(
            "Assert cache size unchanged",
            prevCacheSize == noteListCacheDataSource.getAllNoteLists().size
        )
        // Confirm network size unchanged
        assertTrue(
            "Assert network size unchanged",
            prevNetworkSize == noteListNetworkDataSource.getAllNoteLists(user).size
        )
    }

    @Test
    fun `Delete exception, confirm NOT deleted from cache and network`() = runBlocking {
        // Save cache and network size
        val prevCacheSize = noteListCacheDataSource.getAllNoteLists().size
        val prevNetworkSize = noteListNetworkDataSource.getAllNoteLists(user).size
        // Create note with not existing i
        val noteListToDelete = noteListFactory.createNoteList(
            title = "",
            id = FakeNoteListCacheDataSourceImpl.FORCE_DELETE_NOTE_LIST_EXCEPTION
        )
        // Delete
        deleteNoteList.deleteNoteList(
            noteList = noteListToDelete,
            stateEvent = NoteListStateEvent.DeleteNoteListEvent(noteListToDelete, user),
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
        // Confirm cache size unchanged
        assertTrue(
            "Assert cache size unchanged",
            prevCacheSize == noteListCacheDataSource.getAllNoteLists().size
        )
        // Confirm network size unchanged
        assertTrue(
            "Assert network size unchanged",
            prevNetworkSize == noteListNetworkDataSource.getAllNoteLists(user).size
        )
    }
}