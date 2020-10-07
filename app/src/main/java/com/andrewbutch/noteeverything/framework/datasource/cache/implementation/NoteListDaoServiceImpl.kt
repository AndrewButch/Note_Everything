package com.andrewbutch.noteeverything.framework.datasource.cache.implementation

import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.util.DateUtil
import com.andrewbutch.noteeverything.framework.datasource.cache.abstraction.NoteListDaoService
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NoteListDao
import com.andrewbutch.noteeverything.framework.datasource.cache.mapper.NoteListCacheMapper

class NoteListDaoServiceImpl(
    private val dao: NoteListDao,
    private val mapper: NoteListCacheMapper,
    private val dateUtil: DateUtil
) : NoteListDaoService {
    override suspend fun insertNoteList(noteList: NoteList): Long {
        return dao.insertNoteList(mapper.mapToEntity(noteList, ArrayList()))
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
        TODO("Not yet implemented")
    }

    override suspend fun searchNoteListById(id: String): NoteList? {
        return dao.searchNoteListById(id)?.let {
            val ids = ArrayList<String>()
            for (note in it.notes) {
                ids.add(note.id)
            }
            mapper.mapFromEntity(it, ids)
        }
    }

    override suspend fun getAllNoteLists(): List<NoteList> {
        return mapper.mapFromEntityList(dao.getAllNoteLists())
    }
}