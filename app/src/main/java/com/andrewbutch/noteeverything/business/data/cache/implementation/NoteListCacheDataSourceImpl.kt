package com.andrewbutch.noteeverything.business.data.cache.implementation

import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteListCacheDataSource
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.framework.datasource.cache.abstraction.NoteListDaoService

class NoteListCacheDataSourceImpl(
    private val noteListDaoService: NoteListDaoService
) :
    NoteListCacheDataSource{
    override suspend fun insertNoteList(noteList: NoteList): Long {
        return noteListDaoService.insertNoteList(noteList)
    }

    override suspend fun updateNoteList(
        id: String,
        newTitle: String,
        newColor: String,
        timestamp: String?
    ): Int {
        return noteListDaoService.updateNoteList(
            id, newTitle, newColor, timestamp
        )
    }

    override suspend fun deleteNoteList(id: String): Int {
        return noteListDaoService.deleteNoteList(id)
    }

    override suspend fun deleteAllNoteLists(): Int {
        return noteListDaoService.deleteAllNoteLists()
    }

    override suspend fun searchNoteListById(id: String): NoteList? {
        return noteListDaoService.searchNoteListById(id)
    }

    override suspend fun getAllNoteLists(): List<NoteList> {
        return noteListDaoService.getAllNoteLists()
    }

}