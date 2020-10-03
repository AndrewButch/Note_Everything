package com.andrewbutch.noteeverything.business.data.network

import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.domain.model.Note

class FakeNoteNetworkDataSourceImpl(
    private val data: HashMap<String, Note>
) : NoteNetworkDataSource {
    override suspend fun insertOrUpdateNote(note: Note) {
        data[note.id] = note
    }

    override suspend fun insertOrUpdateNotes(notes: List<Note>) {
        for (note in notes) {
            data[note.id] = note
        }
    }

    override suspend fun deleteNote(id: String) {
        data.remove(id)
    }

    override suspend fun deleteAllNotes() {
        data.clear()
    }

    override suspend fun searchNote(note: Note): Note? = data[note.id]

    override suspend fun getAllNotes(): List<Note> = ArrayList(data.values)
}