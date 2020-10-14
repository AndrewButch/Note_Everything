package com.andrewbutch.noteeverything.business.data.network.abstraction

import com.andrewbutch.noteeverything.business.domain.model.Note

interface NoteNetworkDataSource{

    suspend fun insertOrUpdateNote(note: Note)

    suspend fun deleteNote(note: Note)

    suspend fun deleteNotesByOwnerListId(ownerListId: String)

    suspend fun searchNote(note: Note): Note?

    suspend fun getNotesByOwnerListId(ownerListId: String): List<Note>

    // functions for delete UNDO
//    suspend fun insertDeletedNote(note: Note)
//
//    suspend fun insertDeletedNotes(notes: List<Note>)
//
//    suspend fun deleteDeletedNote(note: Note)
//
//    suspend fun getDeletedNotes(): List<Note>

}