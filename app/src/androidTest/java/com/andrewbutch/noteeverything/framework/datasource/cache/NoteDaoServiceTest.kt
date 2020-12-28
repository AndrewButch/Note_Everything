package com.andrewbutch.noteeverything.framework.datasource.cache

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.util.DateUtil
import com.andrewbutch.noteeverything.di.TestAppComponent
import com.andrewbutch.noteeverything.framework.datasource.cache.abstraction.NoteDaoService
import com.andrewbutch.noteeverything.framework.datasource.cache.database.*
import com.andrewbutch.noteeverything.framework.datasource.cache.implementation.NoteDaoServiceImpl
import com.andrewbutch.noteeverything.framework.datasource.cache.mapper.NoteCacheMapper
import com.andrewbutch.noteeverything.framework.datasource.cache.mapper.NoteListCacheMapper
import com.andrewbutch.noteeverything.framework.datasource.cache.model.NoteListCacheEntity
import com.andrewbutch.noteeverything.framework.datasource.data.NoteDataFactory
import com.andrewbutch.noteeverything.framework.ui.TestBaseApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
@RunWith(AndroidJUnit4ClassRunner::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // for checking inserting test data
class NoteDaoServiceTest {

    // system under test
    private val daoService: NoteDaoService

    // dependency
    private val application: TestBaseApplication =
        ApplicationProvider.getApplicationContext<Context>() as TestBaseApplication

    @Inject
    lateinit var noteDao: NoteDao

    @Inject
    lateinit var noteListDao: NoteListDao

    @Inject
    lateinit var mapper: NoteCacheMapper

    @Inject
    lateinit var dateUtil: DateUtil

    @Inject
    lateinit var dataFactory: NoteDataFactory

    lateinit var ownerNoteList: NoteListCacheEntity // owner NoteList for all Notes

    @Inject
    lateinit var noteListCacheMapper: NoteListCacheMapper // for single NoteList

    init {
        (application.appComponent as TestAppComponent)
            .inject(this)
        daoService = NoteDaoServiceImpl(
            noteDao = noteDao,
            mapper = mapper,
            dateUtil = dateUtil
        )
        insertTestData()
    }

    private fun insertTestData() = runBlocking {
        // get hardcoded NoteLists
        val entityNoteLists = noteListCacheMapper.mapToEntityList(
            dataFactory.produceListOfNoteList()
        )
        // insert NoteLists into Room
        noteListDao.insertMultipleNoteList(entityNoteLists)

        // get hardcoded Notes
        val entityNotes = mapper.mapToEntityList(
            dataFactory.produceListOfNotes()
        )
        // insert Notes into Room
        noteDao.insertMultipleNotes(entityNotes)

        // ger NoteList which will be used for requests
        ownerNoteList = noteListDao.getAllNoteLists().first()
    }

    @Test
    fun a_checkNotEmpty() = runBlocking {
        val dataSize = daoService.getNotesByOwnerListId(ownerNoteList.id).size
        assertTrue(dataSize > 0)
    }

    @Test
    fun insertNoteSuccess_checkContainsInDB() = runBlocking {
        val newNote = dataFactory.createSingleNote(
            id = null,
            title = "Insert NoteList",
            color = null,
            completed = true,
            listId = ownerNoteList.id
        )

        daoService.insertNote(newNote)
        val allNotes = daoService.getNotesByOwnerListId(ownerNoteList.id)
        assertTrue(allNotes.contains(newNote))
    }

    @Test
    fun insertNoteException_checkNotContainsInDB() = runBlocking {
        val newNote = dataFactory.createSingleNote(
            id = null,
            title = "Insert NoteList",
            color = null,
            completed = true,
            listId = "list ID not exists in DB"
        )
        try {
            daoService.insertNote(newNote)
            fail("SQLiteConstraintException expected")
        } catch (e: SQLiteConstraintException) {
            val allNotes = daoService.getNotesByOwnerListId(ownerNoteList.id)
            assertFalse(allNotes.contains(newNote))
        }
    }

    @Test
    fun deleteNote_confirmDeleting() = runBlocking {
        val randomNote = daoService.getNotesByOwnerListId(ownerNoteList.id).shuffled().first()
        daoService.deleteNote(randomNote.id)

        val allNotes = daoService.getNotesByOwnerListId(ownerNoteList.id)
        assertFalse(allNotes.contains(randomNote))
    }

    @Test
    fun deleteMultipleNotes() = runBlocking {
        // get 3 random note list
        val notesToDelete = ArrayList<Note>()
        var allNotes = daoService.getNotesByOwnerListId(ownerNoteList.id).shuffled()
        notesToDelete.add(allNotes[0])
        notesToDelete.add(allNotes[1])
        notesToDelete.add(allNotes[2])

        // delete
        daoService.deleteNotes(
            arrayListOf(
                notesToDelete[0].id,
                notesToDelete[1].id,
                notesToDelete[2].id,
            )
        )

        // confirm deleted
        allNotes = daoService.getNotesByOwnerListId(ownerNoteList.id)
        assertFalse(allNotes.containsAll(notesToDelete))
    }

    // update
    @Test
    fun updateNote() = runBlocking {
        val randomNote = daoService.getNotesByOwnerListId(ownerNoteList.id).shuffled().first()

        randomNote.completed = !randomNote.completed
        randomNote.title = "new title"
        randomNote.color = "#DDD"
        randomNote.updatedAt = dateUtil.getCurrentTimestamp()

        daoService.updateNote(
            id = randomNote.id,
            newTitle = randomNote.title,
            completed = randomNote.completed,
            newColor = randomNote.color,
            timestamp = randomNote.updatedAt
        )

        // get updated
        val updatedNote = daoService.searchNoteById(randomNote.id)

        assertTrue(randomNote == updatedNote)

    }

    @Test
    fun insert1000Note_confirmSizeIncrease() = runBlocking {
        // create array
        val array1000 = ArrayList<Note>()
        array1000.addAll(dataFactory.createMultipleNotes(1000, ownerNoteList.id))

        // save previous size
        val prevSize = daoService.getNotesByOwnerListId(ownerNoteList.id).size

        // insert 1000 items
        daoService.insertNotes(array1000)

        // confirm size increase
        val actualSize = daoService.getNotesByOwnerListId(ownerNoteList.id).size
        assertEquals(prevSize + 1000, actualSize)
    }

    @Test
    fun deleteNoteList_confirmDeleteNotes() = runBlocking {
        // create array
        val array10 = ArrayList<Note>()
        array10.addAll(dataFactory.createMultipleNotes(10, ownerNoteList.id))

        // insert 1000 items
        daoService.insertNotes(array10)

        // confirm inserting
        var allNotes = daoService.getNotesByOwnerListId(ownerNoteList.id)
        assertTrue("Assert inserting", allNotes.containsAll(array10))

        // delete 1 NoteList - owner of 1000 Notes
        noteListDao.deleteNoteList(ownerNoteList.id)

        // confirm delete notes
        allNotes = daoService.getNotesByOwnerListId(ownerNoteList.id)
        assertFalse("Assert deleting", allNotes.containsAll(array10))
    }

    /**
     * 1) Setup filter = NOTE_FILTER_TITLE and order = NOTE_ORDER_ASC
     * 2) Get actual result
     * 3) Get request result
     * 4) Compare actual result with request result
     */
    @Test
    fun getNotesOrderedByTitleAscending() = runBlocking {
        val ownerListId = ownerNoteList.id
        val filterOption = NOTE_FILTER_TITLE
        val orderOption = NOTE_ORDER_ASC
        val actualResult = dataFactory.produceSortedListOfNotesByOwnerList(
            ownerListId = ownerListId,
            filter = filterOption,
            order = orderOption
        )
        val requestResult =
            daoService.getNotesByOwnerListId(ownerListId, filterOption + orderOption)
        for ((i, note) in actualResult.withIndex()) {
            assertTrue("Title + ASC: ", note == requestResult[i])
        }
    }

    /**
     * 1) Setup filter = NOTE_FILTER_TITLE and order = NOTE_ORDER_DESC
     * 2) Get actual result
     * 3) Get request result
     * 4) Compare actual result with request result
     */
    @Test
    fun getNotesOrderedByTitleDescending() = runBlocking {
        val ownerListId = ownerNoteList.id
        val filterOption = NOTE_FILTER_TITLE
        val orderOption = NOTE_ORDER_DESC
        val actualResult = dataFactory.produceSortedListOfNotesByOwnerList(
            ownerListId = ownerListId,
            filter = filterOption,
            order = orderOption
        )
        val requestResult =
            daoService.getNotesByOwnerListId(ownerListId, filterOption + orderOption)
        for ((i, note) in actualResult.withIndex()) {
            assertTrue("Title + DESC: ", note == requestResult[i])
        }
    }

    /**
     * 1) Setup filter = NOTE_FILTER_DATE_CREATED and order = NOTE_ORDER_DESC
     * 2) Get actual result
     * 3) Get request result
     * 4) Compare actual result with request result
     */
    @Test
    fun getNoteOrderedByCreateDateDescending() = runBlocking {
        val ownerListId = ownerNoteList.id
        val filterOption = NOTE_FILTER_DATE_CREATED
        val orderOption = NOTE_ORDER_DESC
        val actualResult = dataFactory.produceSortedListOfNotesByOwnerList(
            ownerListId = ownerListId,
            filter = filterOption,
            order = orderOption
        )
        val requestResult =
            daoService.getNotesByOwnerListId(ownerListId, filterOption + orderOption)
        for ((i, note) in actualResult.withIndex()) {
            assertTrue("Created_at + DESC: ", note == requestResult[i])
        }
    }

    /**
     * 1) Setup filter = NOTE_FILTER_DATE_CREATED and order = NOTE_ORDER_ASC
     * 2) Get actual result
     * 3) Get request result
     * 4) Compare actual result with request result
     */
    @Test
    fun getNoteOrderedByCreateDateAscending() = runBlocking {
        val ownerListId = ownerNoteList.id
        val filterOption = NOTE_FILTER_DATE_CREATED
        val orderOption = NOTE_ORDER_ASC
        val actualResult = dataFactory.produceSortedListOfNotesByOwnerList(
            ownerListId = ownerListId,
            filter = filterOption,
            order = orderOption
        )
        val requestResult =
            daoService.getNotesByOwnerListId(ownerListId, filterOption + orderOption)
        for ((i, note) in actualResult.withIndex()) {
            assertTrue("Created_at + ASC: ", note == requestResult[i])
        }
    }
}