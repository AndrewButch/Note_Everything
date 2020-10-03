package com.andrewbutch.noteeverything.business.data

import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Tests only.
 * Read fake data from json file and produce List<Note>, HashMap<String, Note>
 */
class NoteDataFactory(
    private val testClassLoader: ClassLoader
) {

    // ------------ Note -------------------

    fun produceListOfNotes(): List<Note> {
        val rawJson = getNotesFromFile("notes.json")
        return Gson().fromJson(rawJson, object : TypeToken<List<Note>>() {}.type)
    }

    fun produceHashMapOfNotes(listOfNotes: List<Note>): HashMap<String, Note> {
        val map: HashMap<String, Note> = HashMap()
        for (note in listOfNotes){
            map[note.id] = note
        }
        return map
    }


    // ------------ NoteList -------------------

    fun produceListOfNoteLists(): List<NoteList> {
        val rawJson = getNotesFromFile("list_of_notes.json")
        return Gson().fromJson(rawJson, object : TypeToken<List<NoteList>>() {}.type)
    }

    fun produceHashMapOfNoteLists(listOfNoteLists: List<NoteList>): HashMap<String, NoteList> {
        val map: HashMap<String, NoteList> = HashMap()
        for (noteList in listOfNoteLists){
            map[noteList.id] = noteList
        }
        return map
    }


    // ------------------------------

    private fun getNotesFromFile(fileName: String): String {
        return testClassLoader.getResource(fileName).readText()
    }
}
