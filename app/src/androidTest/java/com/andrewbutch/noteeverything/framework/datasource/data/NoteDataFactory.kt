package com.andrewbutch.noteeverything.framework.datasource.data

import android.app.Application
import android.content.res.AssetManager
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteFactory
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.model.NoteListFactory
import com.andrewbutch.noteeverything.business.domain.util.DateUtil
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NOTE_FILTER_DATE_CREATED
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NOTE_FILTER_TITLE
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NOTE_ORDER_ASC
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class NoteDataFactory
constructor(
    private val application: Application,
    private val noteFactory: NoteFactory,
    private val noteListFactory: NoteListFactory,
    private val dateUtil: DateUtil
) {

    fun produceListOfNotes(): List<Note> {
        val notes: List<Note> = Gson()
            .fromJson(
                getNotesFromFile("notes.json"),
                object : TypeToken<List<Note>>() {}.type
            )
        return notes
    }

    fun produceListOfNoteList(): List<NoteList> {
        val lists: List<NoteList> = Gson()
            .fromJson(
                getNotesFromFile("list_of_notes.json"),
                object : TypeToken<List<NoteList>>() {}.type
            )
        return lists
    }

    private fun getNotesFromFile(fileName: String): String? {
        return readJSONFromAsset(fileName)
    }

    private fun readJSONFromAsset(fileName: String): String? {
        return try {
            val inputStream: InputStream = (application.assets as AssetManager).open(fileName)
            inputStream.bufferedReader().use { it.readText() }
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
    }

    // -------------- Note -------------

    fun createSingleNote(
        id: String? = null,
        title: String,
        completed: Boolean,
        color: String? = null,
        createdAt: String? = null,
        updatedAt: String? = null,
        listId: String
    ) = noteFactory.createNote(id, title, completed, color, createdAt, updatedAt, listId)

    fun createMultipleNotes(numNotes: Int, ownerListId: String) =
        noteFactory.createNoteList(numNotes, ownerListId)


    // -------------- Note list -------------

    fun createSingleNoteList(
        id: String? = null,
        title: String,
        color: String? = null,
        createdAt: String? = null,
        updatedAt: String? = null
    ) = noteListFactory.createNoteList(id, title, color, createdAt, updatedAt)

    fun createMultipleNoteLists(numList: Int): List<NoteList> =
        noteListFactory.createMultipleNoteList(numList)


    fun produceSortedListOfNotesByOwnerList(
        ownerListId: String,
        filter: String,
        order: String
    ): List<Note> {
        val allNotes: List<Note> = produceListOfNotes()
        val notesByOwnerListId: ArrayList<Note> = ArrayList()
        for (note in allNotes) {
            if (note.listId == ownerListId) {
                notesByOwnerListId.add(note)
            }
        }

        var sortedList: List<Note> = emptyList()
        when (filter) {
            NOTE_FILTER_TITLE -> {
                // sort by title
                sortedList = if (order == NOTE_ORDER_ASC) {
                    // ascending order
                    sortByTitleAscending(notesByOwnerListId)
                } else {
                    // descending order
                    sortByTitleDescending(notesByOwnerListId)
                }
            }
            NOTE_FILTER_DATE_CREATED -> {
                // sort by create date
                sortedList = if (order == NOTE_ORDER_ASC) {
                    // ascending order
                    sortByDateCreatedAscending(notesByOwnerListId)

                } else {
                    // descending order
                    sortByDateCreatedDescending(notesByOwnerListId)
                }
            }
        }

        return sortedList
    }

    private fun sortByTitleAscending(list: List<Note>): ArrayList<Note> {
        val sortedSet: SortedSet<Note> = TreeSet { o1, o2 -> o1.title.compareTo(o2.title) }
        sortedSet.addAll(list)
        return ArrayList(sortedSet)
    }

    private fun sortByTitleDescending(list: List<Note>): ArrayList<Note> {
        val sortedSet: SortedSet<Note> = TreeSet { o1, o2 -> o2.title.compareTo(o1.title) }
        sortedSet.addAll(list)
        return ArrayList(sortedSet)
    }

    private fun sortByDateCreatedAscending(list: List<Note>): ArrayList<Note> {
        val sortedSet: SortedSet<Note> = TreeSet { o1, o2 ->
            val o1Date = dateUtil.convertStringDateToFirebaseTimestamp(o1.createdAt)
            val o2Date = dateUtil.convertStringDateToFirebaseTimestamp(o2.createdAt)
            o1Date.compareTo(o2Date) }
        sortedSet.addAll(list)
        return ArrayList(sortedSet)
    }

    private fun sortByDateCreatedDescending(list: List<Note>): ArrayList<Note> {
        val sortedSet: SortedSet<Note> = TreeSet { o1, o2 ->
            val o1Date = dateUtil.convertStringDateToFirebaseTimestamp(o1.createdAt)
            val o2Date = dateUtil.convertStringDateToFirebaseTimestamp(o2.createdAt)
            o2Date.compareTo(o1Date) }
        sortedSet.addAll(list)
        return ArrayList(sortedSet)
    }


}