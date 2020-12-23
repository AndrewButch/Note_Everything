package com.andrewbutch.noteeverything.business.data.network.abstraction

import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.User

interface NoteNetworkDataSource{

    suspend fun insertOrUpdateNote(note: Note, user: User)

    suspend fun deleteNote(note: Note, user: User)

    suspend fun deleteNotesByOwnerListId(ownerListId: String, user: User)

    suspend fun searchNote(note: Note, user: User): Note?

    suspend fun getNotesByOwnerListId(ownerListId: String, user: User): List<Note>

    // functions for delete UNDO
//    suspend fun insertDeletedNote(note: Note)
//
//    suspend fun insertDeletedNotes(notes: List<Note>)
//
//    suspend fun deleteDeletedNote(note: Note)
//
//    suspend fun getDeletedNotes(): List<Note>

}