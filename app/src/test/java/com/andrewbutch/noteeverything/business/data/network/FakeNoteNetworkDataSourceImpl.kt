package com.andrewbutch.noteeverything.business.data.network

import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.domain.model.Note

class FakeNoteNetworkDataSourceImpl(
    private val data: HashMap<String, Note>
) : NoteNetworkDataSource {
    override suspend fun insertOrUpdateNote(note: Note) {
        data[note.id] = note
    }

    override suspend fun deleteNote(note: Note) {
        data.remove(note.id)
    }

    override suspend fun deleteNotesByOwnerListId(ownerListId: String) {
        for (note in data.values) {
            if (note.listId == ownerListId) {
                deleteNote(note)
            }
        }
    }

    override suspend fun searchNote(note: Note): Note? = data[note.id]

    override suspend fun getNotesByOwnerListId(ownerListId: String): List<Note> {
        val notes = ArrayList<Note>()
        for (note in data.values) {
            if (note.listId == ownerListId) {
                notes.add(note)
            }
        }
        return notes
    }
}