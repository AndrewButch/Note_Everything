package com.andrewbutch.noteeverything.framework.datasource

import android.app.Application
import android.content.res.AssetManager
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream

class NoteDataFactory
constructor(
    private val application: Application,
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

}