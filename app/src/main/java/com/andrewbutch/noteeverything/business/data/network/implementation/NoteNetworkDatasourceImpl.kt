package com.andrewbutch.noteeverything.business.data.network.implementation

import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.NoteFirestoreService

class NoteNetworkDatasourceImpl(
    private val noteFirestoreService: NoteFirestoreService
) : NoteNetworkDataSource {
    override suspend fun insertOrUpdateNote(note: Note) {
        return noteFirestoreService.insertOrUpdateNote(note)
    }

    override suspend fun insertOrUpdateNotes(notes: List<Note>) {
        return noteFirestoreService.insertOrUpdateNotes(notes)
    }

    override suspend fun deleteNote(id: String) {
        return noteFirestoreService.deleteNote(id)
    }

    override suspend fun deleteAllNotes() {
        return noteFirestoreService.deleteAllNotes()
    }

    override suspend fun searchNote(note: Note): Note? {
        return noteFirestoreService.searchNote(note)
    }

    override suspend fun getAllNotes(): List<Note> {
        return noteFirestoreService.getAllNotes()
    }

}