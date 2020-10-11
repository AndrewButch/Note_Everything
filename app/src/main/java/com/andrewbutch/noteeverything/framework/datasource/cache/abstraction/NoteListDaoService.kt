package com.andrewbutch.noteeverything.framework.datasource.cache.abstraction

import com.andrewbutch.noteeverything.business.domain.model.NoteList

interface NoteListDaoService {
    suspend fun insertNoteList(noteList: NoteList): Long

    suspend fun insertMultipleNoteList(noteLists: List<NoteList>): LongArray

    suspend fun updateNoteList(
        id: String,
        newTitle: String,
        newColor: String,
        timestamp: String?
    ): Int

    suspend fun deleteNoteList(id: String): Int

    suspend fun deleteAllNoteLists(): Int

    suspend fun searchNoteListById(id: String): NoteList?

    suspend fun getAllNoteLists(): List<NoteList>
}