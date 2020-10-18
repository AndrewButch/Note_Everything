package com.andrewbutch.noteeverything.framework.datasource.data

import android.app.Application
import android.content.res.AssetManager
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteFactory
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.model.NoteListFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteDataFactory
@Inject
constructor(
    private val application: Application,
    private val noteFactory: NoteFactory,
    private val noteListFactory: NoteListFactory
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


}