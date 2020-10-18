package com.andrewbutch.noteeverything.framework.datasource.network

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.andrewbutch.noteeverything.di.TestAppComponent
import com.andrewbutch.noteeverything.framework.datasource.data.NoteDataFactory
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.NoteListFirestoreService
import com.andrewbutch.noteeverything.framework.datasource.network.implementation.NoteListFirestoreServiceImpl
import com.andrewbutch.noteeverything.framework.datasource.network.mapper.NoteListNetworkMapper
import com.andrewbutch.noteeverything.framework.datasource.network.model.NoteListNetworkEntity
import com.andrewbutch.noteeverything.framework.ui.TestBaseApplication
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
@FlowPreview
@RunWith(AndroidJUnit4ClassRunner::class)
class NoteListFirestoreServiceTest {

    // system under test
    private val noteListFirestoreService: NoteListFirestoreService

    // dependency
    private val application: TestBaseApplication =
        ApplicationProvider.getApplicationContext<Context>() as TestBaseApplication

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var dataFactory: NoteDataFactory

    @Inject
    lateinit var mapper: NoteListNetworkMapper

    private val testEntities: List<NoteListNetworkEntity>

    init {
        (application.appComponent as TestAppComponent)
            .inject(this)

        testEntities = mapper.mapToEntityList(dataFactory.produceListOfNoteList())

        noteListFirestoreService = NoteListFirestoreServiceImpl(
            firestore, mapper
        )
    }

    @BeforeTest
    fun beforeTest() {
        insertTestData()
    }
    private fun insertTestData() = runBlocking {
        for (entity in testEntities) {
            firestore
                .collection(NoteListFirestoreServiceImpl.NOTE_LISTS_COLLECTION)
                .document(NoteListFirestoreServiceImpl.USER_ID)
                .collection(NoteListFirestoreServiceImpl.NOTE_LISTS_COLLECTION)
                .document(entity.id)
                .set(entity)
        }
    }

    @AfterTest
    fun afterTest() {
        clearStoreData()
    }

    private fun clearStoreData() = runBlocking {
        val allNoteLists: List<NoteListNetworkEntity> =
            firestore
                .collection(NoteListFirestoreServiceImpl.NOTE_LISTS_COLLECTION)
                .document(NoteListFirestoreServiceImpl.USER_ID)
                .collection(NoteListFirestoreServiceImpl.NOTE_LISTS_COLLECTION)
                .get()
                .await()
                .toObjects(NoteListNetworkEntity::class.java)
        for (noteList in allNoteLists) {
            firestore
                .collection(NoteListFirestoreServiceImpl.NOTE_LISTS_COLLECTION)
                .document(NoteListFirestoreServiceImpl.USER_ID)
                .collection(NoteListFirestoreServiceImpl.NOTE_LISTS_COLLECTION)
                .document(noteList.id)
                .delete()
                .await()
        }
    }


    /**
     *  Insert note list:
     *  1) Insert note list
     *  2) Confirm by search
     */
    @Test
    fun insertSingleNoteList_confirmBySearch() = runBlocking {
        val noteList = dataFactory.createSingleNoteList(title = "new list")
        noteListFirestoreService.insertOrUpdateNoteList(noteList)

        val searchResult = noteListFirestoreService.searchNoteList(noteList)
        assertEquals(noteList, searchResult)
    }

    /**
     * Update note list:
     * 1) get random note list
     * 2) modify
     * 3) update network note list
     * 4) get network updated note list
     * 5) confirm equals
     */

    @Test
    fun getNoteList_update_confirmUpdate() = runBlocking {
        // get random note list
        val randomNoteList = noteListFirestoreService.getAllNoteLists().shuffled().first()

        // modify note list
        val updatedNoteList = dataFactory.createSingleNoteList(
            randomNoteList.id,
            "Updated titile",
            "#CCC",
            randomNoteList.createdAt
        )

        // update network
        noteListFirestoreService.insertOrUpdateNoteList(updatedNoteList)

        // get updated note list
        val updatedNoteResult = noteListFirestoreService.searchNoteList(randomNoteList)

        // confirm updating
        assertEquals(updatedNoteList, updatedNoteResult)
    }

    /**
     * Delete existing note list:
     * 1) get random note list
     * 2) delete note list
     * 3) get deleted note
     * 4) confirm result is null
     */
    @Test
    fun deleteExistingNoteList_confirmDeleting() = runBlocking {
        // get random note list
        val randomNoteList = noteListFirestoreService.getAllNoteLists().shuffled().first()

        // delete note list
        noteListFirestoreService.deleteNoteList(randomNoteList.id)

        // get deleted note
        val deletedNoteResult = noteListFirestoreService.searchNoteList(randomNoteList)

        // confirm result is null
        assertNull(deletedNoteResult)
    }

    /**
     * Delete NOT existing note list:
     * 1) create note list
     * 2) get all note lists and save size
     * 2) try to delete not existing note list
     * 3) check size note change
     * 4) check note list not exists
     */

    @Test
    fun deleteNotExistingNoteLIst_confirmSizeNotChange() = runBlocking {
        // create note list
        val newNoteList = dataFactory.createSingleNoteList(title = "Delete note")

        // get all note lists and save size
        val prevSize = noteListFirestoreService.getAllNoteLists().size

        // try to delete not existing note list
        noteListFirestoreService.deleteNoteList(newNoteList.id)

        // check size note change
        assertEquals(prevSize, noteListFirestoreService.getAllNoteLists().size)

        // check note list not exists
        assertNull(noteListFirestoreService.searchNoteList(newNoteList))
    }


    /**
     * Delete all note lists:
     * 1) confirm note list collection not empty
     * 2) delete all note
     * 3) confirm collection is empty
     */

    @Test
    fun deleteAllNotesList_confirmEmpty() = runBlocking {
        // confirm note list collection not empty
        assertTrue(
            "Confirm initial collection not empty",
            noteListFirestoreService.getAllNoteLists().isNotEmpty()
        )

        val randomNoteList = noteListFirestoreService.getAllNoteLists().shuffled().first()


        // delete all note
        noteListFirestoreService.deleteAllNotesLists()

        assertNull(
            "Confirm random note list is null",
            noteListFirestoreService.searchNoteList(randomNoteList)
        )

        // confirm collection is empty
        assertTrue("Confirm empty", noteListFirestoreService.getAllNoteLists().isEmpty())

    }

    /**
     * Search existing note list:
     * 1) get all note lists and pick up random note list
     * 2) search single note list
     * 3) confirm equals
     */

    @Test
    fun searchExistingNoteList_confirmEquals() = runBlocking {
        // get all note lists and pick up random note list
        val randomNoteList = noteListFirestoreService.getAllNoteLists().shuffled().first()

        // search single note list
        val searchResult = noteListFirestoreService.searchNoteList(randomNoteList)

        // confirm equals
        assertEquals(randomNoteList, searchResult)
    }


    /**
     * Search NOT existing note list:
     * 1) create note list
     * 2) search new list
     * 3) confirm search result null
     */

    @Test
    fun searchNoteExistingNoteList_confirmNull() = runBlocking {
        // create note list
        val newNoteList = dataFactory.createSingleNoteList(title = "Not exists")

        // search new list
        val searchResult = noteListFirestoreService.searchNoteList(newNoteList)

        // confirm search result null
        assertNull(searchResult)
    }


    /**
     *  1) Get all note lists
     *  2) Check result size
     */
    @Test
    fun getAllNoteLists_confirmSize() = runBlocking {
        val noteLists = noteListFirestoreService.getAllNoteLists()
        assertEquals(testEntities.size, noteLists.size)
    }
}