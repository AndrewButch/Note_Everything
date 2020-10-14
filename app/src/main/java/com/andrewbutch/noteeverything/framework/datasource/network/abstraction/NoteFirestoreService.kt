package com.andrewbutch.noteeverything.framework.datasource.network.abstraction

import com.andrewbutch.noteeverything.business.domain.model.Note

interface NoteFirestoreService {

    suspend fun insertOrUpdateNote(note: Note)

    suspend fun deleteNote(note: Note)

    suspend fun deleteNotesByOwnerListId(ownerListId: String)

    suspend fun searchNote(note: Note): Note?

    suspend fun getNotesByOwnerListId(ownerListId: String): List<Note>
}