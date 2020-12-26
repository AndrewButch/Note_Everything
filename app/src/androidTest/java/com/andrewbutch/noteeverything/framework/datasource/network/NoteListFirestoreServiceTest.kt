package com.andrewbutch.noteeverything.framework.datasource.network

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.andrewbutch.noteeverything.business.domain.model.User
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

    private fun insertTestData() = runBlocking {
        for (entity in testEntities) {
            firestore
                .collection(NoteListFirestoreServiceImpl.NOTE_LISTS_COLLECTION)
                .document(USER_ID)
                .collection(NoteListFirestoreServiceImpl.NOTE_LISTS_COLLECTION)
                .document(entity.id)
                .set(entity)
                .await()

        }
    }

    private fun clearStoreData() = runBlocking {
        val allNoteLists: List<NoteListNetworkEntity> =
            firestore
                .collection(NoteListFirestoreServiceImpl.NOTE_LISTS_COLLECTION)
                .document(USER_ID)
                .collection(NoteListFirestoreServiceImpl.NOTE_LISTS_COLLECTION)
                .get()
                .await()
                .toObjects(NoteListNetworkEntity::class.java)
        for (noteList in allNoteLists) {
            firestore
                .collection(NoteListFirestoreServiceImpl.NOTE_LISTS_COLLECTION)
                .document(USER_ID)
                .collection(NoteListFirestoreServiceImpl.NOTE_LISTS_COLLECTION)
                .document(noteList.id)
                .delete()
                .await()
        }
    }

    @BeforeTest
    fun beforeTest() {
        insertTestData()
    }

    @BeforeTest
    fun afterTest() {
        clearStoreData()
    }


    /**
     *  Insert note list:
     *  1) create note list
     *  2) insert note list
     *  3) confirm by search
     */
    @Test
    fun insertSingleNoteList_confirmBySearch() = runBlocking {
        // create note list
        val noteList = dataFactory.createSingleNoteList(title = "new list")

        // insert note list
        noteListFirestoreService.insertOrUpdateNoteList(noteList, user)

        // confirm by search
        val searchResult = noteListFirestoreService.searchNoteList(noteList, user)
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
        val randomNoteList = mapper.mapFromEntity(testEntities.shuffled().first())

        // modify note list
        val updatedNoteList = dataFactory.createSingleNoteList(
            randomNoteList.id,
            "Updated titile",
            "#CCC",
            randomNoteList.createdAt
        )

        // update network
        noteListFirestoreService.insertOrUpdateNoteList(updatedNoteList, user)

        // get updated note list
        val updatedNoteResult = noteListFirestoreService.searchNoteList(randomNoteList, user)

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
        val randomNoteList = mapper.mapFromEntity(testEntities.shuffled().first())

        // delete note list
        noteListFirestoreService.deleteNoteList(randomNoteList.id, user)

        // get deleted note
        val deletedNoteResult = noteListFirestoreService.searchNoteList(randomNoteList, user)

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
        val prevSize = noteListFirestoreService.getAllNoteLists(user).size

        // try to delete not existing note list
        noteListFirestoreService.deleteNoteList(newNoteList.id, user)

        // check size note change
        assertEquals(prevSize, noteListFirestoreService.getAllNoteLists(user).size)

        // check note list not exists
        assertNull(noteListFirestoreService.searchNoteList(newNoteList, user))
    }


    /**
     * Delete all note lists:
     * 1) confirm note list collection not empty
     * 2) delete all note
     * 3) confirm collection is empty
     * 4)
     */
    @Test
    fun deleteAllNotesList_confirmEmpty() = runBlocking {
        // confirm note list collection not empty
        assertTrue(
            "Confirm initial collection not empty",
            noteListFirestoreService.getAllNoteLists(user).isNotEmpty()
        )

        // delete all note
        noteListFirestoreService.deleteAllNotesLists(user)

        val randomNoteList = mapper.mapFromEntity(testEntities.shuffled().first())
        val networkResult = noteListFirestoreService.searchNoteList(randomNoteList, user)
        assertNull("Confirm random note list is null", networkResult)

        // confirm collection is empty
        assertTrue("Confirm empty", noteListFirestoreService.getAllNoteLists(user).isEmpty())

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
        val randomNoteList = noteListFirestoreService.getAllNoteLists(user).shuffled().first()

        // search single note list
        val searchResult = noteListFirestoreService.searchNoteList(randomNoteList, user)

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
        val searchResult = noteListFirestoreService.searchNoteList(newNoteList, user)

        // confirm search result null
        assertNull(searchResult)
    }


    /**
     *  1) Get all note lists
     *  2) Check result size
     */
    @Test
    fun getAllNoteLists_confirmSize() = runBlocking {
        val noteLists = noteListFirestoreService.getAllNoteLists(user)
        assertEquals(testEntities.size, noteLists.size)
    }
    
    companion object {
        // User ID for firestore path
        private val USER_ID = "12345"
        private val user = User(id = USER_ID, displayName = null, email = null)
    }
}