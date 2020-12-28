package com.andrewbutch.noteeverything.business.interactors.notelist

import com.andrewbutch.noteeverything.business.data.cache.FakeNoteCacheDataSourceImpl.Companion.FORCE_DELETE_NOTE_EXCEPTION
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteFactory
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.DataState
import com.andrewbutch.noteeverything.business.interactors.notelist.DeleteMultipleNotes.Companion.DELETE_MULTIPLE_NOTES_FAILURE
import com.andrewbutch.noteeverything.business.interactors.notelist.DeleteMultipleNotes.Companion.DELETE_MULTIPLE_NOTES_SUCCESS
import com.andrewbutch.noteeverything.di.DependencyContainer
import com.andrewbutch.noteeverything.framework.datasource.cache.database.ORDER_BY_DESC_DATE_CREATED
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListStateEvent
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListViewState
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Test cases
 * 1. Delete notes success, confirm deletion from cache and network
 *      1.1 get 3 random notes for deleting
 *      1.2 delete
 *      1.3 confirm DELETE_MULTIPLE_NOTES_SUCCESS message emitted from flow
 *      1.4 confirm notes are deleted from cache
 *      1.5 confirm notes are deleted from network
 * 2. Delete notes errors, confirm correct deleting
 * In this case we attempt to delete all notes passed as input, but result
 * message is DELETE_MULTIPLE_NOTES_FAILURE. Note with the fail message
 * will not be deleted, but other notes will be deleted.
 *      2.1 get 2 random notes for deleting
 *      2.2 create 2 note, so they will cause error when deleting
 *      2.3 confirm DELETE_MULTIPLE_NOTES_FAILURE message emitted from flow
 *      2.4 confirm only the valid notes are deleted from cache
 *      2.5 confirm cache size
 *      2.6 confirm only the valid notes are deleted from network
 *      2.7 confirm network size
 * 3. Delete exception, confirm cache and network unchanged
 *      3.1 get 3 random notes for deleting
 *      3.2 change id FORCE_DELETE_NOTE_EXCEPTION of one note
 *      3.3 confirm DELETE_MULTIPLE_NOTES_FAILURE message emitted from flow
 *      3.4 confirm only the valid notes are deleted from cache
 *      3.5 confirm cache size
 *      3.6 confirm only the valid notes are deleted from network
 *      3.7 confirm network size
 */

class DeleteMultipleNotesTest {

    // test object
    private lateinit var deleteMultipleNotes: DeleteMultipleNotes

    // dependency
    private var dependencyContainer = DependencyContainer()
    private lateinit var noteCacheDataSource: NoteCacheDataSource
    private lateinit var noteNetworkDataSource: NoteNetworkDataSource
    private lateinit var noteFactory: NoteFactory

    private val ownerListId = "cfc3414d-5778-4abc-8a2d-d38dbc2c18ae"
    private val user = User("jLfWxedaCBdpxvcdfVpdzQIfzDw2", "", "")
    private val filterAndOrder = ORDER_BY_DESC_DATE_CREATED


    @Before
    fun before() {
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteNetworkDataSource = dependencyContainer.noteNetworkDataSource
        noteFactory = dependencyContainer.noteFactory
        deleteMultipleNotes = DeleteMultipleNotes(noteCacheDataSource, noteNetworkDataSource)
    }


    @Test
    fun `Delete notes success, confirm deletion from cache and network`() = runBlocking {
        // get 3 random notes for deleting
        val allNotesByOwnerIdShuffled =
            noteCacheDataSource.getNotesByOwnerListId(ownerListId, filterAndOrder).shuffled()
        val notesToDelete = listOf<Note>(
            allNotesByOwnerIdShuffled[0],
            allNotesByOwnerIdShuffled[1],
            allNotesByOwnerIdShuffled[2]
        )
        // delete
        deleteMultipleNotes.deleteMultipleNotes(
            notes = notesToDelete,
            stateEvent = NoteListStateEvent.DeleteMultipleNotesEvent(notesToDelete, user),
            user = user
        ).collect {
            object : FlowCollector<DataState<NoteListViewState>?> {
                override suspend fun emit(value: DataState<NoteListViewState>?) {
                    // confirm DELETE_MULTIPLE_NOTES_SUCCESS message emitted from flow
                    assertTrue(
                        DELETE_MULTIPLE_NOTES_SUCCESS == value?.stateMessage?.message
                    )
                }

            }
        }
        // confirm notes are deleted from cache
        for (note in notesToDelete) {
            assertNull(noteCacheDataSource.searchNoteById(note.id))
        }
        // confirm notes are deleted from network
        for (note in notesToDelete) {
            assertNull(noteNetworkDataSource.searchNote(note, user))
        }
    }


    @Test
    fun `Delete notes errors, confirm correct deleting`() = runBlocking {
        val cacheSizeBefore =
            noteCacheDataSource
                .getNotesByOwnerListId(ownerListId, filterAndOrder)
                .size
        val networkSizeBefore =
            noteNetworkDataSource
                .getNotesByOwnerListId(ownerListId, user)
                .size

        // get 2 random notes for deleting
        val allNotesByOwnerIdShuffled =
            noteCacheDataSource.getNotesByOwnerListId(ownerListId, filterAndOrder).shuffled()
        val validNotesToDelete = ArrayList<Note>()
        validNotesToDelete.add(allNotesByOwnerIdShuffled[0])
        validNotesToDelete.add(allNotesByOwnerIdShuffled[1])

        // create 2 note, so they will cause error when deleting
        val invalidNotesToDelete = ArrayList<Note>()
        invalidNotesToDelete.add(noteFactory.createNote(title = "", listId = ownerListId))
        invalidNotesToDelete.add(noteFactory.createNote(title = "", listId = ownerListId))

        val notesToDelete = ArrayList(validNotesToDelete + invalidNotesToDelete)
        deleteMultipleNotes.deleteMultipleNotes(
            notes = notesToDelete,
            stateEvent = NoteListStateEvent.DeleteMultipleNotesEvent(notesToDelete, user),
            user = user
        ).collect {
            object : FlowCollector<DataState<NoteListViewState>?> {
                override suspend fun emit(value: DataState<NoteListViewState>?) {
                    // confirm DELETE_MULTIPLE_NOTES_FAILURE message emitted from flow
                    assertTrue(
                        "Assert failure message",
                        DELETE_MULTIPLE_NOTES_FAILURE == value?.stateMessage?.message
                    )
                }

            }
        }
        // confirm only the valid notes are deleted from cache
        val cacheNotes = noteCacheDataSource.getNotesByOwnerListId(ownerListId, filterAndOrder)
        assertFalse(
            "Assert valid notes deleted from cache",
            cacheNotes.containsAll(validNotesToDelete)
        )
        // confirm cache size
        assertTrue(
            "Assert cache size",
            cacheSizeBefore == cacheNotes.size + validNotesToDelete.size
        )

        // confirm only the valid notes are deleted from network
        val networkNotes = noteNetworkDataSource.getNotesByOwnerListId(ownerListId, user)
        assertFalse(
            "Assert valid notes deleted from network",
            networkNotes.containsAll(validNotesToDelete)
        )
        // confirm network size
        assertTrue(
            "Assert network size",
            networkSizeBefore == networkNotes.size + validNotesToDelete.size
        )

    }


    @Test
    fun `Delete exception, confirm cache and network unchanged`() = runBlocking {
        val cacheSizeBefore = noteCacheDataSource.getNotesByOwnerListId(ownerListId, filterAndOrder).size
        val networkSizeBefore = noteNetworkDataSource.getNotesByOwnerListId(ownerListId, user).size

        // get 2 random notes for deleting
        val allNotesByOwnerIdShuffled =
            noteCacheDataSource.getNotesByOwnerListId(ownerListId, filterAndOrder).shuffled()
        val validNotesToDelete = ArrayList<Note>()
        validNotesToDelete.add(allNotesByOwnerIdShuffled[0])
        validNotesToDelete.add(allNotesByOwnerIdShuffled[1])

        // create 2 note, so they will cause error when deleting
        val invalidNotesToDelete = ArrayList<Note>()
        invalidNotesToDelete.add(
            noteFactory.createNote(
                id = FORCE_DELETE_NOTE_EXCEPTION,
                title = "",
                listId = ownerListId
            )
        )
        invalidNotesToDelete.add(
            noteFactory.createNote(
                id = FORCE_DELETE_NOTE_EXCEPTION,
                title = "",
                listId = ownerListId
            )
        )

        val notesToDelete = ArrayList(validNotesToDelete + invalidNotesToDelete)
        deleteMultipleNotes.deleteMultipleNotes(
            notes = notesToDelete,
            stateEvent = NoteListStateEvent.DeleteMultipleNotesEvent(notesToDelete, user),
            user = user
        ).collect {
            object : FlowCollector<DataState<NoteListViewState>?> {
                override suspend fun emit(value: DataState<NoteListViewState>?) {
                    // confirm DELETE_MULTIPLE_NOTES_FAILURE message emitted from flow
                    assertTrue(
                        "Assert failure message",
                        DELETE_MULTIPLE_NOTES_FAILURE == value?.stateMessage?.message
                    )
                }

            }
        }
        // confirm only the valid notes are deleted from cache
        val cacheNotes = noteCacheDataSource.getNotesByOwnerListId(ownerListId, filterAndOrder)
        assertFalse(
            "Assert valid notes deleted from cache",
            cacheNotes.containsAll(validNotesToDelete)
        )
        // confirm cache size
        assertTrue(
            "Assert cache size",
            cacheSizeBefore == cacheNotes.size + validNotesToDelete.size
        )

        // confirm only the valid notes are deleted from network
        val networkNotes = noteNetworkDataSource.getNotesByOwnerListId(ownerListId, user)
        assertFalse(
            "Assert valid notes deleted from network",
            networkNotes.containsAll(validNotesToDelete)
        )
        // confirm network size
        assertTrue(
            "Assert network size",
            networkSizeBefore == networkNotes.size + validNotesToDelete.size
        )
    }
}