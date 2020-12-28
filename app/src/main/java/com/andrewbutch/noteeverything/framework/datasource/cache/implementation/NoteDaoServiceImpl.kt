package com.andrewbutch.noteeverything.framework.datasource.cache.implementation

import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.util.DateUtil
import com.andrewbutch.noteeverything.framework.datasource.cache.abstraction.NoteDaoService
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NoteDao
import com.andrewbutch.noteeverything.framework.datasource.cache.database.ORDER_BY_ASC_DATE_CREATED
import com.andrewbutch.noteeverything.framework.datasource.cache.database.ORDER_BY_ASC_TITLE
import com.andrewbutch.noteeverything.framework.datasource.cache.database.ORDER_BY_DESC_TITLE
import com.andrewbutch.noteeverything.framework.datasource.cache.mapper.NoteCacheMapper
import com.andrewbutch.noteeverything.framework.datasource.cache.model.NoteCacheEntity

class NoteDaoServiceImpl
constructor(
    private val noteDao: NoteDao,
    private val mapper: NoteCacheMapper,
    private val dateUtil: DateUtil
) : NoteDaoService {
    override suspend fun insertNote(note: Note): Long {
        return noteDao.insertNote(mapper.mapToEntity(note))
    }

    override suspend fun insertNotes(notes: List<Note>): LongArray {
        return noteDao.insertMultipleNotes(mapper.mapToEntityList(notes))
    }

    override suspend fun deleteNote(id: String): Int {
        return noteDao.deleteNote(id)
    }

    override suspend fun deleteNotes(notes: List<String>): Int {
        return noteDao.deleteNotes(notes)
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

    override suspend fun getNotesByOwnerListId(
        ownerListId: String,
        filterAndOrder: String?
    ): List<Note> {
        return mapper.mapFromEntityList(getNotesByFilterAndOrder(ownerListId, filterAndOrder))
    }

    override suspend fun searchNoteById(id: String): Note? {
        return noteDao.searchNoteById(id)?.let {
            mapper.mapFromEntity(it)
        }
    }

    private suspend fun getNotesByFilterAndOrder(
        ownerListId: String,
        filterAndOrder: String?
    ): List<NoteCacheEntity> {
        return when {
            // Default ORDER_BY_DESC_DATE_UPDATED
            filterAndOrder.isNullOrEmpty() -> {
                noteDao.searchNotesOrderByDateDESC(ownerId = ownerListId)
            }
            filterAndOrder.contains(ORDER_BY_ASC_DATE_CREATED) -> {
                noteDao.searchNotesOrderByDateASC(ownerId = ownerListId)
            }

            filterAndOrder.contains(ORDER_BY_DESC_TITLE) -> {
                noteDao.searchNotesOrderByTitleDESC(ownerId = ownerListId)
            }

            filterAndOrder.contains(ORDER_BY_ASC_TITLE) -> {
                noteDao.searchNotesOrderByTitleASC(ownerId = ownerListId)
            }
            // in other cases use Default ORDER_BY_DESC_DATE_UPDATED
            else -> noteDao.searchNotesOrderByDateDESC(ownerId = ownerListId)
        }
    }


}