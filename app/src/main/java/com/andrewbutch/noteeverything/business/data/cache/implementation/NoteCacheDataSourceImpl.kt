package com.andrewbutch.noteeverything.business.data.cache.implementation

import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteCacheDataSource
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.framework.datasource.cache.abstraction.NoteDaoService

class NoteCacheDataSourceImpl(private val noteDaoService: NoteDaoService) : NoteCacheDataSource {
    override suspend fun insertNote(note: Note): Long {
        return noteDaoService.insertNote(note)
    }

    override suspend fun deleteNote(id: String): Int {
        return noteDaoService.deleteNote(id)
    }

    override suspend fun deleteNotes(notes: List<String>): Int {
        return noteDaoService.deleteNotes(notes)
    }

    override suspend fun updateNote(
        id: String,
        newTitle: String,
        completed: Boolean,
        newColor: String,
        timestamp: String?
    ): Int {
        return noteDaoService.updateNote(id, newTitle, completed, newColor, timestamp)
    }

    override suspend fun getAllNotes(): List<Note> {
        return noteDaoService.getAllNotes()
    }

    override suspend fun searchNoteById(id: String): Note? {
        return noteDaoService.searchNoteById(id)
    }


    override suspend fun insertNotes(notes: List<Note>): LongArray {
        return noteDaoService.insertNotes(notes)
    }
}