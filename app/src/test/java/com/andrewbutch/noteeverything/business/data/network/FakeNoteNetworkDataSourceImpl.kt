package com.andrewbutch.noteeverything.business.data.network

import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.User

class FakeNoteNetworkDataSourceImpl(
    private val data: HashMap<String, Note>
) : NoteNetworkDataSource {
    override suspend fun insertOrUpdateNote(note: Note, user: User) {
        data[note.id] = note
    }

    override suspend fun deleteNote(note: Note, user: User) {
        data.remove(note.id)
    }

    override suspend fun deleteNotesByOwnerListId(ownerListId: String, user: User) {
        for (note in data.values) {
            if (note.listId == ownerListId) {
                deleteNote(note, user)
            }
        }
    }

    override suspend fun searchNote(note: Note, user: User): Note? = data[note.id]

    override suspend fun getNotesByOwnerListId(ownerListId: String, user: User): List<Note> {
        val notes = ArrayList<Note>()
        for (note in data.values) {
            if (note.listId == ownerListId) {
                notes.add(note)
            }
        }
        return notes
    }
}