package com.andrewbutch.noteeverything.business.data.network.abstraction

import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.model.User

interface NoteListNetworkDataSource {

    suspend fun insertOrUpdateNoteList(noteList: NoteList, user: User)

    suspend fun deleteNoteList(id: String, user: User)

    suspend fun deleteAllNotesLists(user: User)

    suspend fun searchNoteList(noteList: NoteList, user: User): NoteList?

    suspend fun getAllNoteLists(user: User): List<NoteList>
}