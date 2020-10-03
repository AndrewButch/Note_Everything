package com.andrewbutch.noteeverything.business.data.cache.abstraction

import com.andrewbutch.noteeverything.business.domain.model.NoteList

interface NoteListCacheDataSource {

    suspend fun insertNoteList(noteList: NoteList): Long

    suspend fun updateNoteList(title: String, color: String): Int

    suspend fun deleteNoteList(id: String)

    suspend fun deleteAllNoteLists()

    suspend fun searchNoteListById(id: String)

    suspend fun getAllNoteLists(): List<NoteList>

}