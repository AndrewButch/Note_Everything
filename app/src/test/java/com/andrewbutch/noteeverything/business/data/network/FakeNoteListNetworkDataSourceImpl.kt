package com.andrewbutch.noteeverything.business.data.network

import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteListNetworkDataSource
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.model.User

class FakeNoteListNetworkDataSourceImpl(
    private val data: HashMap<String, NoteList>
) : NoteListNetworkDataSource {
    override suspend fun insertOrUpdateNoteList(noteList: NoteList, user: User) {
        data[noteList.id] = noteList
    }

    override suspend fun deleteNoteList(id: String, user: User) {
        data.remove(id)
    }

    override suspend fun deleteAllNotesLists(user: User) {
        data.clear()
    }

    override suspend fun searchNoteList(noteList: NoteList, user: User): NoteList? = data[noteList.id]

    override suspend fun getAllNoteLists(user: User): List<NoteList> = ArrayList(data.values)
}