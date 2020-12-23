package com.andrewbutch.noteeverything.framework.datasource.network.abstraction

import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.User

interface NoteFirestoreService {

    suspend fun insertOrUpdateNote(note: Note, user: User)

    suspend fun deleteNote(note: Note, user: User)

    suspend fun deleteNotesByOwnerListId(ownerListId: String, user: User)

    suspend fun searchNote(note: Note, user: User): Note?

    suspend fun getNotesByOwnerListId(ownerListId: String, user: User): List<Note>
}