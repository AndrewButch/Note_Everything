package com.andrewbutch.noteeverything.business.data.cache.abstraction

import com.andrewbutch.noteeverything.business.domain.model.NoteList

interface NoteListCacheDataSource {

    suspend fun insertNoteList(noteList: NoteList): Long

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