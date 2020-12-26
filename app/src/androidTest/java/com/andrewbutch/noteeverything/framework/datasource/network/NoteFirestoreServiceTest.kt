package com.andrewbutch.noteeverything.framework.datasource.network

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.di.TestAppComponent
import com.andrewbutch.noteeverything.framework.datasource.data.NoteDataFactory
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.NoteFirestoreService
import com.andrewbutch.noteeverything.framework.datasource.network.implementation.NoteFirestoreServiceImpl
import com.andrewbutch.noteeverything.framework.datasource.network.implementation.NoteFirestoreServiceImpl.Companion.NOTES_COLLECTION
import com.andrewbutch.noteeverything.framework.datasource.network.implementation.NoteListFirestoreServiceImpl.Companion.NOTE_LISTS_COLLECTION
import com.andrewbutch.noteeverything.framework.datasource.network.mapper.NoteListNetworkMapper
import com.andrewbutch.noteeverything.framework.datasource.network.mapper.NoteNetworkMapper
import com.andrewbutch.noteeverything.framework.datasource.network.model.NoteListNetworkEntity
import com.andrewbutch.noteeverything.framework.datasource.network.model.NoteNetworkEntity
import com.andrewbutch.noteeverything.framework.ui.TestBaseApplication
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Use with local firestore emulator
 * https://firebase.google.com/docs/emulator-suite/install_and_configure?hl=en
 * Start emulator command: firebase emulators:start --only firestore
 * */

@ExperimentalCoroutinesApi
@FlowPreview
@RunWith(AndroidJUnit4ClassRunner::class)
class NoteFirestoreServiceTest {


    // system under test
    private val noteFirestoreService: NoteFirestoreService

    // dependency
    private val application: TestBaseApplication =
        ApplicationProvider.getApplicationContext<Context>() as TestBaseApplication

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var dataFactory: NoteDataFactory

    @Inject
    lateinit var noteMapper: NoteNetworkMapper

    @Inject
    lateinit var noteListMapper: NoteListNetworkMapper

    private lateinit var ownerNoteList: NoteListNetworkEntity

    private val testNoteListEntities: List<NoteListNetworkEntity>

    // Test Notes for inserting @Before test
    private val testNoteEntities: List<NoteNetworkEntity>

    init {
        (application.appComponent as TestAppComponent)
            .inject(this)

        testNoteListEntities = noteListMapper.mapToEntityList(dataFactory.produceListOfNoteList())

        testNoteEntities = noteMapper.mapToEntityList(dataFactory.produceListOfNotes())

        noteFirestoreService = NoteFirestoreServiceImpl(
            firestore, noteMapper
        )
        removeNoteListEntities()
        removeNoteEntities()
    }

    // Remove notes
    private fun removeNoteListEntities() = runBlocking {
        for (entity in testNoteListEntities) {
            firestore
                .collection(NOTE_LISTS_COLLECTION)
                .document(USER_ID)
                .collection(NOTE_LISTS_COLLECTION)
                .document(entity.id)
                .delete()
                .await()

        }
    }

    // Remove note lists
    private fun removeNoteEntities() = runBlocking {
        for (entity in testNoteEntities) {
            val notes = firestore
                .collection(NOTES_COLLECTION)
                .document(USER_ID)
                .collection(NOTES_COLLECTION)
                .whereEqualTo("listId", entity.listId)
                .get()
                .await()
                .toObjects(NoteNetworkEntity::class.java)

            for (note in notes) {
                firestore
                    .collection(NOTES_COLLECTION)
                    .document(USER_ID)
                    .collection(NOTES_COLLECTION)
                    .document(note.id)
                    .delete()
                    .await()
            }
        }
    }


    private fun insertTestOwnerNoteListEntities() = runBlocking {
        for (entity in testNoteListEntities) {
            firestore
                .collection(NOTE_LISTS_COLLECTION)
                .document(USER_ID)
                .collection(NOTE_LISTS_COLLECTION)
                .document(entity.id)
                .set(entity)
                .await()
        }
    }

    private fun insertTestNoteEntities() = runBlocking {
        for (entity in testNoteEntities) {
            firestore
                .collection(NOTES_COLLECTION)
                .document(USER_ID)
                .collection(NOTES_COLLECTION)
                .document(entity.id)
                .set(entity)
                .await()
        }
    }

    private fun printNotesByOwnerListId(listId: String) = runBlocking {
        val notes = firestore
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .whereEqualTo("listId", listId)
            .get()
            .await()
            .toObjects(NoteNetworkEntity::class.java)
        for ((index, note) in notes.withIndex()) {
            log("Note $index: $note")
        }
    }

    private fun log(msg: String) {
        Log.d(TEST_TAG, "Thread: ${Thread.currentThread().name}: $msg ")
    }

    @Before
    fun beforeTest() {
        insertTestOwnerNoteListEntities()
        insertTestNoteEntities()
        ownerNoteList = testNoteListEntities.shuffled().first()
    }

    @After
    fun afterTest() {
        removeNoteListEntities()
        removeNoteEntities()
    }

    /**
     * Insert note:
     * 1) create note
     * 2) insert note
     * 3) confirm by search
     */
    @Test
    fun insertNote_confirmBySearch() = runBlocking {
        // create note
        val newNote = dataFactory.createSingleNote(
            title = "New note",
            completed = false,
            listId = ownerNoteList.id
        )

        // insert note
        noteFirestoreService.insertOrUpdateNote(newNote, user)

        printNotesByOwnerListId(newNote.listId)
        // confirm by search
        val searchResult = noteFirestoreService.searchNote(newNote, user)
        assertTrue(newNote == searchResult)
    }

    /**
     * Update note:
     * 1) get random note from test data
     * 2) assert note exists in network
     * 3) modify
     * 4) update network note
     * 5) get updated note from network
     * 6) confirm equals
     */
    @Test
    fun updateNote_confirmBySearch() = runBlocking {
        // get random note from test data
        val randomNote = noteMapper.mapFromEntity(testNoteEntities.shuffled().first())
        var searchResult = noteFirestoreService.searchNote(randomNote, user)

        // assert note exists in network
        assertTrue(randomNote == searchResult)

        // modify
        randomNote.title = "Updated title"
        randomNote.completed = !randomNote.completed
        randomNote.color = "#AAA"

        // update network note
        noteFirestoreService.insertOrUpdateNote(randomNote, user)

        // get updated note from network
        searchResult = noteFirestoreService.searchNote(randomNote, user)

        // confirm equals
        assertTrue(randomNote == searchResult)
    }

    /**
     * delete note:
     * 1) get random note from test data
     * 2) assert note exists in network
     * 2) delete from network
     * 3) get deleted note from network
     * 4) confirm result null
     */
    @Test
    fun deleteNote_confirmResultEmpty() = runBlocking {
        // get random note from test data
        val randomNote = noteMapper.mapFromEntity(testNoteEntities.shuffled().first())
        var searchResult = noteFirestoreService.searchNote(randomNote, user)

        // assert note exists in network
        assertTrue(randomNote == searchResult)

        // delete from network
        noteFirestoreService.deleteNote(randomNote, user)

        // get deleted note from network
        searchResult = noteFirestoreService.searchNote(randomNote, user)

        // confirm result null
        assertNull(searchResult)
    }

    /**
     * delete notes by owner list id
     * 1) get random note list id
     * 2) confirm note list collection non empty
     * 3) delete all notes with owner id
     * 4) search notes with list id
     * 5) confirm network result is empty
     */
    @Test
    fun deleteNotes_confirmResultEmpty() = runBlocking {
        // get random note list id
        val randomNoteListId = testNoteListEntities.shuffled().first().id

        // confirm note list collection non empty
        var networkResult = noteFirestoreService.getNotesByOwnerListId(randomNoteListId, user)
        assertTrue("Collection is non empty", networkResult.isNotEmpty())

        // delete all notes with owner id
        noteFirestoreService.deleteNotesByOwnerListId(randomNoteListId, user)

        // search notes with list id
        networkResult = noteFirestoreService.getNotesByOwnerListId(randomNoteListId, user)

        // confirm network result is empty
        assertTrue("Result empty?", networkResult.isEmpty())
    }


    /**
     * search note:
     * 1) get note from test data
     * 2) search
     * 3) confirm equals
     */
    @Test
    fun searchNote_confirmEquals() = runBlocking {
        // get note from test data
        val randomNote = noteMapper.mapFromEntity(testNoteEntities.shuffled().first())

        // search
        val searchResult = noteFirestoreService.searchNote(randomNote, user)

        // confirm equals
        assertTrue(randomNote == searchResult)
    }

    /**
     * get notes by owner list id
     * 1) get note list id from test data
     * 2) get actual result
     * 2) get network result
     * 3) confirm result non empty
     * 3) confirm actual contains network
     */
    @Test
    fun searchNotesByListId_confirmContainsAll() = runBlocking() {
        // get note list id from test data
        val randomNoteListId = testNoteListEntities.shuffled().first().id

        // get actual result
        val actualResult = noteMapper.mapFromEntityList(getNotesByListId(randomNoteListId))
        // get network result
        val networkResult = noteFirestoreService.getNotesByOwnerListId(randomNoteListId, user)

        // confirm result non empty
        assertTrue("Result empty?", networkResult.isNotEmpty())
        // confirm actual contains network
        assertTrue(
            "Actual result contains all network result",
            actualResult.containsAll(networkResult)
        )
    }

    private fun getNotesByListId(listId: String): List<NoteNetworkEntity> {
        val list = ArrayList<NoteNetworkEntity>()
        for (entity in testNoteEntities) {
            if (entity.listId == listId) {
                list.add(entity)
            }
        }
        return list
    }


    companion object {
        private val TEST_TAG = "!@#TEST"

        // User ID for firestore path
        private val USER_ID = "12345"
        private val user = User(id = USER_ID, displayName = null, email = null)
    }
}
