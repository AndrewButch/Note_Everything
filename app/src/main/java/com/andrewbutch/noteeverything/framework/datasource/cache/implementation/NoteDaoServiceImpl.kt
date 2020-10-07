package com.andrewbutch.noteeverything.framework.datasource.cache.implementation

import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.util.DateUtil
import com.andrewbutch.noteeverything.framework.datasource.cache.abstraction.NoteDaoService
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NoteDao
import com.andrewbutch.noteeverything.framework.datasource.cache.mapper.NoteCacheMapper

class NoteDaoServiceImpl(
    private val noteDao: NoteDao,
    private val mapper: NoteCacheMapper,
    private val dateUtil: DateUtil
) : NoteDaoService {
    override suspend fun insertNote(note: Note): Long {
        return noteDao.insertNote(mapper.mapToEntity(note))
    }

    override suspend fun insertNotes(notes: List<Note>): LongArray {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNote(id: String): Int {
        return noteDao.deleteNote(id)
    }

    override suspend fun deleteNotes(notes: List<Note>): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateNote(
        id: String,
        newTitle: String,
        completed: Boolean,
        newColor: String,
        timestamp: String?
    ): Int {
        return if (timestamp == null) {
            val currentTimestamp = dateUtil.getCurrentTimestamp()
            noteDao.updateNote(id, newTitle, completed, newColor, currentTimestamp)
        } else {
            noteDao.updateNote(id, newTitle, completed, newColor, timestamp)
        }
    }

    override suspend fun getAllNotes(): List<Note> {
        return mapper.mapFromEntityList(noteDao.getAllNotes())
    }

    override suspend fun searchNoteById(id: String): Note? {
        return noteDao.searchNoteById(id)?.let {
            mapper.mapFromEntity(it)
        }
    }

    override suspend fun getNumNotes(): Int {
        TODO("Not yet implemented")
    }
}