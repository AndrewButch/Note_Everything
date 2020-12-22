package com.andrewbutch.noteeverything.business.interactors.notelistdetail

import com.andrewbutch.noteeverything.business.data.cache.CacheConstants
import com.andrewbutch.noteeverything.business.data.cache.FakeNoteListCacheDataSourceImpl.Companion.FORCE_UPDATE_NOTE_LIST_EXCEPTION
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteListCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteListNetworkDataSource
import com.andrewbutch.noteeverything.business.domain.model.NoteListFactory
import com.andrewbutch.noteeverything.business.domain.state.DataState
import com.andrewbutch.noteeverything.business.interactors.notelistdetail.UpdateNoteList.Companion.UPDATE_NOTE_LIST_FAILURE
import com.andrewbutch.noteeverything.business.interactors.notelistdetail.UpdateNoteList.Companion.UPDATE_NOTE_LIST_SUCCESS
import com.andrewbutch.noteeverything.di.DependencyContainer
import com.andrewbutch.noteeverything.framework.ui.notelistdetail.state.NoteListDetailStateEvent
import com.andrewbutch.noteeverything.framework.ui.notelistdetail.state.NoteListDetailViewState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

/**
 * Test cases
 * 1. Update success, confirm cache and network update
 *      1.1 get random note list
 *      1.2 change title
 *      1.3 update
 *      1.4 confirm UPDATE_NOTE_LIST_SUCCESS message
 *      1.5 confirm cache update
 *      1.6 confirm network update
 * 2. Update failure, confirm cache and network unchanged
 *      2.1 create new note
 *      2.2 try update
 *      2.3 confirm UPDATE_NOTE_LIST_FAILURE message
 *      2.4 confirm note doesn`t exist in cache
 *      2.5 confirm note doesn`t exist in network
 * 3. Update exception, confirm cache and network unchanged
 *      3.1 create new note with id = FORCE_UPDATE_NOTE_LIST_EXCEPTION
 *      3.2 try update
 *      3.3 confirm message contain CACHE_ERROR_UNKNOWN
 *      3.4 confirm note doesn`t exist in cache
 *      3.5 confirm note doesn`t exist in network
 */

@InternalCoroutinesApi
class UpdateNoteListTest {

    // Test class
    private val updateNoteList: UpdateNoteList

    // Dependency
    private val dependencyContainer = DependencyContainer()
    private val noteListCacheDataSource: NoteListCacheDataSource
    private val noteListNetworkDataSource: NoteListNetworkDataSource
    private val noteListFactory: NoteListFactory

    init {
        dependencyContainer.build()
        noteListCacheDataSource = dependencyContainer.noteListCacheDataSource
        noteListNetworkDataSource = dependencyContainer.noteListNetworkDataSource
        noteListFactory = dependencyContainer.noteListFactory

        updateNoteList = UpdateNoteList(noteListCacheDataSource, noteListNetworkDataSource)
    }

    @Test
    fun `Update success, confirm cache and network update`() = runBlocking {
        val newTitle = "updated title"
        // get random note list
        val randomNote = noteListCacheDataSource.getAllNoteLists().shuffled().first()
        // change title
        randomNote.title = newTitle
        // update
        updateNoteList.updateNoteList(
            noteList = randomNote,
            stateEvent = NoteListDetailStateEvent.UpdateNoteListEvent()
        ).collect(
            object : FlowCollector<DataState<NoteListDetailViewState>?> {
                override suspend fun emit(value: DataState<NoteListDetailViewState>?) {
                    // confirm UPDATE_NOTE_LIST_SUCCESS message
                    assertTrue(
                        UPDATE_NOTE_LIST_SUCCESS == value?.stateMessage?.message
                    )

                    // confirm cache update
                    val updatedCache = noteListCacheDataSource.searchNoteListById(randomNote.id)
                    updatedCache?.let {
                        assertTrue("Assert cache title", randomNote.title == updatedCache.title)
                        assertTrue("Assert cache color", randomNote.color == updatedCache.color)
                        assertTrue(
                            "Assert cache updated time >",
                            randomNote.updatedAt != updatedCache.updatedAt
                        )
                    } ?: fail("Updated note list from cache is null")

                    // confirm network update
                    val updatedNetwork = noteListNetworkDataSource.searchNoteList(randomNote)
                    updatedNetwork?.let {
                        assertTrue("Assert network title", randomNote.title == updatedNetwork.title)
                        assertTrue("Assert network color", randomNote.color == updatedNetwork.color)
                        assertTrue(
                            "Assert network updated time >",
                            randomNote.updatedAt != updatedNetwork.updatedAt
                        )
                    } ?: fail("Updated note list from cache is null")
                }
            }
        )
    }

    @Test
    fun `Update failure, confirm cache and network unchanged`() = runBlocking {
        // create new note
        val newNote = noteListFactory.createNoteList(title = "")

        // try update
        updateNoteList.updateNoteList(
            noteList = newNote,
            stateEvent = NoteListDetailStateEvent.UpdateNoteListEvent()
        ).collect(
            object : FlowCollector<DataState<NoteListDetailViewState>?> {
                override suspend fun emit(value: DataState<NoteListDetailViewState>?) {
                    // confirm UPDATE_NOTE_LIST_FAILURE message
                    assertTrue(
                        UPDATE_NOTE_LIST_FAILURE == value?.stateMessage?.message
                    )

                    // confirm note doesn`t exist in cache
                    val updatedCache = noteListCacheDataSource.searchNoteListById(newNote.id)
                    assertNull(updatedCache)

                    // confirm note doesn`t exist in network
                    val updatedNetwork = noteListNetworkDataSource.searchNoteList(newNote)
                    assertNull(updatedNetwork)
                }
            }
        )
    }

    @Test
    fun `Update exception, confirm cache and network unchanged`() = runBlocking {
        // create new note with id = FORCE_UPDATE_NOTE_LIST_EXCEPTION
        val newNote =
            noteListFactory.createNoteList(id = FORCE_UPDATE_NOTE_LIST_EXCEPTION, title = "")

        // try update
        updateNoteList.updateNoteList(
            noteList = newNote,
            stateEvent = NoteListDetailStateEvent.UpdateNoteListEvent()
        ).collect(
            object : FlowCollector<DataState<NoteListDetailViewState>?> {
                override suspend fun emit(value: DataState<NoteListDetailViewState>?) {
                    // confirm message contain CACHE_ERROR_UNKNOWN
                    assertTrue(
                        value?.stateMessage?.message
                            ?.contains(CacheConstants.CACHE_ERROR_UNKNOWN)
                            ?: false
                    )

                    // confirm note doesn`t exist in cache
                    val updatedCache = noteListCacheDataSource.searchNoteListById(newNote.id)
                    assertNull(updatedCache)

                    // confirm note doesn`t exist in network
                    val updatedNetwork = noteListNetworkDataSource.searchNoteList(newNote)
                    assertNull(updatedNetwork)
                }
            }
        )
    }
}
