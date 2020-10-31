package com.andrewbutch.noteeverything.business.data.network

import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteListNetworkDataSource
import com.andrewbutch.noteeverything.business.domain.model.NoteList

class FakeNoteListNetworkDataSourceImpl(
    private val data: HashMap<String, NoteList>
) : NoteListNetworkDataSource {
    override suspend fun insertOrUpdateNoteList(noteList: NoteList) {
        data[noteList.id] = noteList
    }

    override suspend fun deleteNoteList(id: String) {
        data.remove(id)
    }

    override suspend fun deleteAllNotesLists() {
        data.clear()
    }

    override suspend fun searchNoteList(noteList: NoteList): NoteList? = data[noteList.id]

    override suspend fun getAllNoteLists(): List<NoteList> = ArrayList(data.values)
}