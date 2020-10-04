package com.andrewbutch.noteeverything.framework.datasource.cache.abstraction

import com.andrewbutch.noteeverything.business.domain.model.Note

interface NoteDaoService {
    suspend fun insertNote(note: Note): Long

    suspend fun insertNotes(notes: List<Note>): LongArray

    suspend fun deleteNote(id: String): Int

    suspend fun deleteNotes(notes: List<Note>): Int

    suspend fun updateNote(
        id: String,
        newTitle: String,
        completed: Boolean,
        newColor: String,
        timestamp: String?
    ): Int

    suspend fun getAllNotes(): List<Note>

    suspend fun searchNoteById(id: String): Note?

    suspend fun getNumNotes(): Int
}