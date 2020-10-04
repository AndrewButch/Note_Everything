package com.andrewbutch.noteeverything.framework.datasource.network.abstraction

import com.andrewbutch.noteeverything.business.domain.model.NoteList

interface NoteListFirestoreService {

    suspend fun insertOrUpdateNoteList(noteList: NoteList)

    suspend fun deleteNoteList(id: String)

    suspend fun deleteAllNotesLists()

    suspend fun searchNoteList(noteList: NoteList): NoteList?

    suspend fun getAllNotes(): List<NoteList>
}