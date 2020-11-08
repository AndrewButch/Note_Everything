package com.andrewbutch.noteeverything.framework.datasource.cache.implementation

import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.util.DateUtil
import com.andrewbutch.noteeverything.framework.datasource.cache.abstraction.NoteListDaoService
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NoteListDao
import com.andrewbutch.noteeverything.framework.datasource.cache.mapper.NoteListCacheMapper

class NoteListDaoServiceImpl
constructor(
    private val dao: NoteListDao,
    private val mapper: NoteListCacheMapper,
    private val dateUtil: DateUtil
) : NoteListDaoService {
    override suspend fun insertNoteList(noteList: NoteList): Long {
        return dao.insertNoteList(mapper.mapToEntity(noteList))
    }

    override suspend fun insertMultipleNoteList(noteLists: List<NoteList>): LongArray {
        return dao.insertMultipleNoteList(mapper.mapToEntityList(noteLists))
    }

    override suspend fun updateNoteList(
        id: String,
        newTitle: String,
        newColor: String,
        timestamp: String?
    ): Int {
        return if (timestamp == null) {
            val currentTimestamp = dateUtil.getCurrentTimestamp()
            dao.updateNoteList(id, newTitle, newColor, currentTimestamp)
        } else {
            dao.updateNoteList(id, newTitle, newColor, timestamp)
        }
    }

    override suspend fun deleteNoteList(id: String): Int {
        return dao.deleteNoteList(id)
    }

    override suspend fun deleteAllNoteLists(): Int {
        return dao.deleteAllNoteLists()
    }

    override suspend fun searchNoteListById(id: String): NoteList? {
        return dao.searchNoteListById(id)?.let {

            mapper.mapFromEntity(it)
        }
    }

    override suspend fun getAllNoteLists(): List<NoteList> {
        return mapper.mapFromEntityList(dao.getAllNoteLists())
    }
}