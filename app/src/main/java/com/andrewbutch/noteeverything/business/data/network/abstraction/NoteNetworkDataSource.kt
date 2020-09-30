package com.andrewbutch.noteeverything.business.data.network.abstraction

import com.andrewbutch.noteeverything.business.model.Note

interface NoteNetworkDataSource{

    suspend fun insertOrUpdateNote(note: Note)

    suspend fun insertOrUpdateNotes(notes: List<Note>)

    suspend fun deleteNote(primaryKey: String)

    suspend fun deleteAllNotes()

    suspend fun searchNote(note: Note): Note?

    suspend fun getAllNotes(): List<Note>

    // functions for delete UNDO
//    suspend fun insertDeletedNote(note: Note)
//
//    suspend fun insertDeletedNotes(notes: List<Note>)
//
//    suspend fun deleteDeletedNote(note: Note)
//
//    suspend fun getDeletedNotes(): List<Note>

}