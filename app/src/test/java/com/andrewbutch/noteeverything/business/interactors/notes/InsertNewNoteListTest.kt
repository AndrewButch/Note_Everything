package com.andrewbutch.noteeverything.business.interactors.notes

import com.andrewbutch.noteeverything.business.data.cache.CacheConstants
import com.andrewbutch.noteeverything.business.data.cache.FakeNoteListCacheDataSourceImpl
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteListCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteListNetworkDataSource
import com.andrewbutch.noteeverything.business.domain.model.NoteListFactory
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.DataState
import com.andrewbutch.noteeverything.business.interactors.notes.InsertNewNoteList.Companion.INSERT_NOTE_LIST_FAILED
import com.andrewbutch.noteeverything.business.interactors.notes.InsertNewNoteList.Companion.INSERT_NOTE_LIST_SUCCESS
import com.andrewbutch.noteeverything.di.DependencyContainer
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListStateEvent
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListViewState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.util.*

@InternalCoroutinesApi
class InsertNewNoteListTest {
    private val TAG = "InsertNewNoteListTest"

    // Test class
    private val insertNoteList: InsertNewNoteList

    // Dependency
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

        insertNoteList = InsertNewNoteList(
            noteListCacheDataSource, noteListNetworkDataSource, noteListFactory
        )
    }

    /**
     * Test use cases
     * 1) Insert NoteList success(return 1), confirm cache and network update
     * 2) Insert NoteList failure(return -1), confirm cache and network NOT update
     * 3) Insert NoteList exception, confirm cache and network NOT update
     */

    @Test
    fun `Insert NoteList success(return 1), confirm cache and network update`() = runBlocking {
        val newNoteList = noteListFactory.createNoteList(
            title = UUID.randomUUID().toString()
        )

        insertNoteList.insertNewNote(
            id = newNoteList.id,
            title = newNoteList.title,
            color = newNoteList.color,
            stateEvent = NoteListStateEvent.InsertNewNoteListEvent(
                title = newNoteList.title,
                user = user
            ),
            user = user
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    "Insert success message ",
                    INSERT_NOTE_LIST_SUCCESS,
                    value?.stateMessage?.message
                )
            }

        })

        // confirm cache updated
        val insertedCache = noteListCacheDataSource.searchNoteListById(newNoteList.id)
        assertTrue("Cache success update", newNoteList == insertedCache)

        // confirm network updated
        val insertedNetwork = noteListNetworkDataSource.searchNoteList(newNoteList, user)
        assertTrue("Network success update", newNoteList == insertedNetwork)
    }


    @Test
    fun `Insert NoteList failure(return -1), confirm cache and network NOT update`() = runBlocking {
        val newNoteList = noteListFactory.createNoteList(
            id = FakeNoteListCacheDataSourceImpl.FORCE_FAILURE,
            title = UUID.randomUUID().toString()
        )

        insertNoteList.insertNewNote(
            id = newNoteList.id,
            title = newNoteList.title,
            color = newNoteList.color,
            stateEvent = NoteListStateEvent.InsertNewNoteListEvent(
                title = newNoteList.title,
                user = user
            ),
            user = user
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    "Insert failure message ",
                    INSERT_NOTE_LIST_FAILED,
                    value?.stateMessage?.message
                )
            }

        })

        // confirm cache NOT updated
        val insertedCache = noteListCacheDataSource.searchNoteListById(newNoteList.id)
        assertNull("Cache not update", insertedCache)

        // confirm network NOT updated
        val insertedNetwork = noteListNetworkDataSource.searchNoteList(newNoteList, user)
        assertNull("Network not update", insertedNetwork)
    }

    @Test
    fun `Insert NoteList exception, confirm cache and network NOT update`() = runBlocking {
        val newNoteList = noteListFactory.createNoteList(
            id = FakeNoteListCacheDataSourceImpl.FORCE_INSERT_NOTE_LIST_EXCEPTION,
            title = UUID.randomUUID().toString()
        )

        insertNoteList.insertNewNote(
            id = newNoteList.id,
            title = newNoteList.title,
            color = newNoteList.color,
            stateEvent = NoteListStateEvent.InsertNewNoteListEvent(
                title = newNoteList.title,
                user = user
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

        // confirm cache NOT updated
        val insertedCache = noteListCacheDataSource.searchNoteListById(newNoteList.id)
        assertNull("Cache not update", insertedCache)

        // confirm network NOT updated
        val insertedNetwork = noteListNetworkDataSource.searchNoteList(newNoteList, user)
        assertNull("Network not update", insertedNetwork)
    }
}