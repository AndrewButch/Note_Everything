package com.andrewbutch.noteeverything.business.interactors.notedetail

import com.andrewbutch.noteeverything.business.data.cache.CacheConstants
import com.andrewbutch.noteeverything.business.data.cache.FakeNoteCacheDataSourceImpl.Companion.FORCE_UPDATE_NOTE_EXCEPTION
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.domain.model.NoteFactory
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.DataState
import com.andrewbutch.noteeverything.business.interactors.notedetail.UpdateNote.Companion.UPDATE_NOTE_FAILURE
import com.andrewbutch.noteeverything.business.interactors.notedetail.UpdateNote.Companion.UPDATE_NOTE_SUCCESS
import com.andrewbutch.noteeverything.di.DependencyContainer
import com.andrewbutch.noteeverything.framework.datasource.cache.database.ORDER_BY_DESC_DATE_CREATED
import com.andrewbutch.noteeverything.framework.ui.notedetail.state.NoteDetailStateEvent
import com.andrewbutch.noteeverything.framework.ui.notedetail.state.NoteDetailViewState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

/**
 * Test cases
 * 1. Update success, confirm cache and network update
 *      1.1 get random note list
 *      1.2 change title and completed fields
 *      1.3 update
 *      1.4 confirm UPDATE_NOTE_SUCCESS message
 *      1.5 confirm cache update
 *      1.6 confirm network update
 * 2. Update failure, confirm cache and network unchanged
 *      2.1 create new note
 *      2.2 try update
 *      2.3 confirm UPDATE_NOTE_FAILURE message
 *      2.4 confirm note doesn`t exist in cache
 *      2.5 confirm note doesn`t exist in network
 *
 * 3. Update exception, confirm cache and network unchanged
 *      3.1 create new note with id = FORCE_UPDATE_NOTE_EXCEPTION
 *      3.2 try update
 *      3.3 confirm message contain CACHE_ERROR_UNKNOWN
 *      3.4 confirm note doesn`t exist in cache
 *      3.5 confirm note doesn`t exist in network
 */

@InternalCoroutinesApi
class UpdateNoteTest {

    // test object
    private val updateNote: UpdateNote<NoteDetailViewState>

    // Dependency
    private val dependencyContainer = DependencyContainer()
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteNetworkDataSource: NoteNetworkDataSource
    private val noteFactory: NoteFactory

    private val ownerListId = "cfc3414d-5778-4abc-8a2d-d38dbc2c18ae"
    private val user = User("jLfWxedaCBdpxvcdfVpdzQIfzDw2", "", "")
    private val filterAndOrder = ORDER_BY_DESC_DATE_CREATED


    init {
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteNetworkDataSource = dependencyContainer.noteNetworkDataSource
        noteFactory = dependencyContainer.noteFactory

        updateNote = UpdateNote(noteCacheDataSource, noteNetworkDataSource)
    }

    @Test
    fun `Update success, confirm cache and network update`() = runBlocking {
        val newTitle = "updated title"
        // get random note list
        val randomNote = noteCacheDataSource
            .getNotesByOwnerListId(ownerListId, filterAndOrder)
            .shuffled()
            .first()
        // change title
        randomNote.title = newTitle
        randomNote.completed = !randomNote.completed
        // update
        updateNote.updateNote(
            note = randomNote,
            stateEvent = NoteDetailStateEvent.UpdateNoteEvent(randomNote, user),
            user = user
        ).collect(
            object : FlowCollector<DataState<NoteDetailViewState>?> {
                override suspend fun emit(value: DataState<NoteDetailViewState>?) {
                    // confirm UPDATE_NOTE_SUCCESS message
                    assertTrue(
                        UPDATE_NOTE_SUCCESS == value?.stateMessage?.message
                    )

                    // confirm cache update
                    val updatedCache = noteCacheDataSource.searchNoteById(randomNote.id)
                    updatedCache?.let {
                        assertTrue("Assert cache title", randomNote.title == updatedCache.title)
                        assertTrue("Assert cache color", randomNote.color == updatedCache.color)
                        assertTrue(
                            "Assert cache updated time >",
                            randomNote.updatedAt != updatedCache.updatedAt
                        )
                    } ?: fail("Updated note list from cache is null")

                    // confirm network update
                    val updatedNetwork = noteNetworkDataSource.searchNote(randomNote, user)
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
        val newNote = noteFactory.createNote(title = "", listId = ownerListId)

        // try update
        updateNote.updateNote(
            note = newNote,
            stateEvent = NoteDetailStateEvent.UpdateNoteEvent(newNote, user),
            user = user
        ).collect(
            object : FlowCollector<DataState<NoteDetailViewState>?> {
                override suspend fun emit(value: DataState<NoteDetailViewState>?) {
                    // confirm UPDATE_NOTE_FAILURE message
                    assertTrue(
                        UPDATE_NOTE_FAILURE == value?.stateMessage?.message
                    )

                    // confirm note doesn`t exist in cache
                    val updatedCache = noteCacheDataSource.searchNoteById(newNote.id)
                    assertNull(updatedCache)

                    // confirm note doesn`t exist in network
                    val updatedNetwork = noteNetworkDataSource.searchNote(newNote, user)
                    assertNull(updatedNetwork)
                }
            }
        )
    }

    @Test
    fun `Update exception, confirm cache and network unchanged`() = runBlocking {
        // create new note with id = FORCE_UPDATE_NOTE_EXCEPTION
        val newNote =
            noteFactory.createNote(
                id = FORCE_UPDATE_NOTE_EXCEPTION,
                title = "",
                listId = ownerListId
            )

        // try update
        updateNote.updateNote(
            note = newNote,
            stateEvent = NoteDetailStateEvent.UpdateNoteEvent(newNote, user),
            user = user
        ).collect(
            object : FlowCollector<DataState<NoteDetailViewState>?> {
                override suspend fun emit(value: DataState<NoteDetailViewState>?) {
                    // confirm message contain CACHE_ERROR_UNKNOWN
                    assertTrue(
                        value?.stateMessage?.message
                            ?.contains(CacheConstants.CACHE_ERROR_UNKNOWN)
                            ?: false
                    )

                    // confirm note doesn`t exist in cache
                    val updatedCache = noteCacheDataSource.searchNoteById(newNote.id)
                    assertNull(updatedCache)

                    // confirm note doesn`t exist in network
                    val updatedNetwork = noteNetworkDataSource.searchNote(newNote, user)
                    assertNull(updatedNetwork)
                }
            }
        )
    }
}