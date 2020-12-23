package com.andrewbutch.noteeverything.business.data.network.implementation

import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.NoteFirestoreService

class NoteNetworkDatasourceImpl
constructor(
    private val noteFirestoreService: NoteFirestoreService
) : NoteNetworkDataSource {
    override suspend fun insertOrUpdateNote(note: Note, user: User) {
        return noteFirestoreService.insertOrUpdateNote(note, user)
    }

    override suspend fun deleteNote(note: Note, user: User) {
        return noteFirestoreService.deleteNote(note, user)
    }

    override suspend fun deleteNotesByOwnerListId(ownerListId: String, user: User) {
        return noteFirestoreService.deleteNotesByOwnerListId(ownerListId, user)
    }

    override suspend fun searchNote(note: Note, user: User): Note? {
        return noteFirestoreService.searchNote(note, user)
    }

    override suspend fun getNotesByOwnerListId(ownerListId: String, user: User): List<Note> {
        return noteFirestoreService.getNotesByOwnerListId(ownerListId, user)
    }

}