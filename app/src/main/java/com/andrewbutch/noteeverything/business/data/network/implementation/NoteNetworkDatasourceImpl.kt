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

    override suspend fun deleteNote(note: Note) {
        return noteFirestoreService.deleteNote(note)
    }

    override suspend fun deleteNotesByOwnerListId(ownerListId: String) {
        return noteFirestoreService.deleteNotesByOwnerListId(ownerListId)
    }

    override suspend fun searchNote(note: Note): Note? {
        return noteFirestoreService.searchNote(note)
    }

    override suspend fun getNotesByOwnerListId(ownerListId: String): List<Note> {
        return noteFirestoreService.getNotesByOwnerListId(ownerListId)
    }

}