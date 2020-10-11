package com.andrewbutch.noteeverything.framework.datasource.cache

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.util.DateUtil
import com.andrewbutch.noteeverything.di.TestAppComponent
import com.andrewbutch.noteeverything.framework.datasource.cache.abstraction.NoteListDaoService
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NoteListDao
import com.andrewbutch.noteeverything.framework.datasource.cache.implementation.NoteListDaoServiceImpl
import com.andrewbutch.noteeverything.framework.datasource.cache.mapper.NoteListCacheMapper
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
class NoteListDaoServiceTest {

    // system under test
    private val daoService: NoteListDaoService

    // dependency
    private val application: TestBaseApplication =
        ApplicationProvider.getApplicationContext<Context>() as TestBaseApplication

    @Inject
    lateinit var noteListDao: NoteListDao

    @Inject
    lateinit var mapper: NoteListCacheMapper

    @Inject
    lateinit var dateUtil: DateUtil

    @Inject
    lateinit var dataFactory: NoteDataFactory


    init {
        (application.appComponent as TestAppComponent)
            .inject(this)
        daoService = NoteListDaoServiceImpl(
            dao = noteListDao,
            mapper = mapper,
            dateUtil = dateUtil
        )
        insertTestData()
    }

    private fun insertTestData() = runBlocking {
        val entityNoteLists = mapper.mapToEntityList(
            dataFactory.produceListOfNoteList()
        )
        noteListDao.insertMultipleNoteList(entityNoteLists)
    }

    @Test
    fun a_checkNotEmpty() = runBlocking {
        val dataSize = daoService.getAllNoteLists().size
        assertTrue(dataSize > 0)
    }

    @Test
    fun insertNoteList_checkContainsInDB() = runBlocking {
        val newNoteList = dataFactory.createSingleNoteList(
            id = null,
            title = "Insert NoteList",
            color = null
        )
        daoService.insertNoteList(newNoteList)
        val allNoteLists = daoService.getAllNoteLists()
        assertTrue(allNoteLists.contains(newNoteList))
    }

    @Test
    fun deleteRandomNoteLists_confirmDelete() = runBlocking {
        // get 3 random note list
        val noteListsToDelete = ArrayList<NoteList>()
        var allNoteLists = daoService.getAllNoteLists().shuffled()
        noteListsToDelete.add(allNoteLists[0])
        noteListsToDelete.add(allNoteLists[1])
        noteListsToDelete.add(allNoteLists[2])

        // delete
        for (noteList in noteListsToDelete) {
            daoService.deleteNoteList(noteList.id)
        }

        // confirm deleted
        allNoteLists = daoService.getAllNoteLists()
        assertFalse(allNoteLists.containsAll(noteListsToDelete))
    }

    @Test
    fun deleteAllNoteLists() = runBlocking {
        // Get all noteLists
        var allNoteLists = daoService.getAllNoteLists()

        // Assert size > 0
        assertTrue(allNoteLists.isNotEmpty())

        // Delete
        daoService.deleteAllNoteLists()

        // Assert size 0
        allNoteLists = daoService.getAllNoteLists()
        assertTrue(allNoteLists.isEmpty())
    }

    @Test
    fun getRandomNoteList_updateDB_confirmUpdate() = runBlocking {
        // Get random NoteList
        val randomNoteList = daoService.getAllNoteLists().shuffled().first()

        // Change NoteList
        randomNoteList.title = "Updated title"
        randomNoteList.color = "#F00"
        randomNoteList.updatedAt = dateUtil.getCurrentTimestamp()

        daoService.updateNoteList(
            id = randomNoteList.id,
            newTitle = randomNoteList.title,
            newColor = randomNoteList.color,
            timestamp = randomNoteList.updatedAt
        )

        // get updated NoteList
        val updatedNoteList = daoService.searchNoteListById(randomNoteList.id)

        assertTrue(randomNoteList == updatedNoteList)
    }

    @Test
    fun insert1000NoteList_confirmSizeIncrease() = runBlocking {
        // create array
        val array1000 = ArrayList<NoteList>()
        array1000.addAll(dataFactory.createMultipleNoteLists(1000))

        // save previous size
        val prevSize = daoService.getAllNoteLists().size

        // insert 1000 items
        daoService.insertMultipleNoteList(array1000)

        // confirm size increase
        val actualSize = daoService.getAllNoteLists().size
        assertEquals(prevSize + 1000, actualSize)
    }

}