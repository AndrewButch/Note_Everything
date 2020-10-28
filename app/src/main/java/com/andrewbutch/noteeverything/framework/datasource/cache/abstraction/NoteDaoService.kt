package com.andrewbutch.noteeverything.framework.datasource.cache.abstraction

import com.andrewbutch.noteeverything.business.domain.model.Note

interface NoteDaoService {
    suspend fun insertNote(note: Note): Long

    suspend fun insertNotes(notes: List<Note>): LongArray

    suspend fun deleteNote(id: String): Int

    suspend fun deleteNotes(notes: List<String>): Int

    suspend fun updateNote(
        id: String,
        newTitle: String,
        completed: Boolean,
        newColor: String,
        timestamp: String?
    ): Int

    suspend fun getNotesByOwnerListId(ownerListId: String): List<Note>

    suspend fun searchNoteById(id: String): Note?
}