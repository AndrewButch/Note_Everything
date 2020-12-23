package com.andrewbutch.noteeverything.business.data.network.implementation

import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteListNetworkDataSource
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.NoteListFirestoreService

class NoteListNetworkDataSourceImpl
constructor(
    private val noteListFirestoreService: NoteListFirestoreService

) : NoteListNetworkDataSource {
    override suspend fun insertOrUpdateNoteList(noteList: NoteList, user: User) {
        return noteListFirestoreService.insertOrUpdateNoteList(noteList, user)
    }

    override suspend fun deleteNoteList(id: String, user: User) {
        return noteListFirestoreService.deleteNoteList(id, user)
    }

    override suspend fun deleteAllNotesLists(user: User) {
        return noteListFirestoreService.deleteAllNotesLists(user)
    }

    override suspend fun searchNoteList(noteList: NoteList, user: User): NoteList? {
        return noteListFirestoreService.searchNoteList(noteList, user)
    }

    override suspend fun getAllNoteLists(user: User): List<NoteList> {
        return noteListFirestoreService.getAllNoteLists(user)
    }
}