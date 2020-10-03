package com.andrewbutch.noteeverything.business.data.cache.abstraction

import com.andrewbutch.noteeverything.business.domain.model.Note

interface NoteCacheDataSource {

    suspend fun insertNote(note: Note): Long

    suspend fun deleteNote(id: String): Int

    suspend fun deleteNotes(notes: List<Note>): Int

    suspend fun updateNote(
        id: String,
        newTitle: String,
        completed: Boolean,
        newColor: String
    ): Int

//    suspend fun searchNotes(
//        query: String,
//        filterAndOrder: String,
//        page: Int
//    ): List<Note>

    suspend fun getAllNotes(): List<Note>

    suspend fun searchNoteById(id: String): Note?

    suspend fun getNumNotes(): Int

    suspend fun insertNotes(notes: List<Note>): LongArray
}