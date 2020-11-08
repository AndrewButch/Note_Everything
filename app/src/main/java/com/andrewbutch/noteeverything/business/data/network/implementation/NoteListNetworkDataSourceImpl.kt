package com.andrewbutch.noteeverything.business.data.network.implementation

import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteListNetworkDataSource
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.NoteListFirestoreService

class NoteListNetworkDataSourceImpl
constructor(
    private val noteListFirestoreService: NoteListFirestoreService

) : NoteListNetworkDataSource {
    override suspend fun insertOrUpdateNoteList(noteList: NoteList) {
        return noteListFirestoreService.insertOrUpdateNoteList(noteList)
    }

    override suspend fun deleteNoteList(id: String) {
        return noteListFirestoreService.deleteNoteList(id)
    }

    override suspend fun deleteAllNotesLists() {
        return noteListFirestoreService.deleteAllNotesLists()
    }

    override suspend fun searchNoteList(noteList: NoteList): NoteList? {
        return noteListFirestoreService.searchNoteList(noteList)
    }

    override suspend fun getAllNoteLists(): List<NoteList> {
        return noteListFirestoreService.getAllNoteLists()
    }
}