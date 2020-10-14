package com.andrewbutch.noteeverything.business.data.cache

import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteCacheDataSource
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.util.DateUtil

class FakeNoteCacheDataSourceImpl(
    private val data: HashMap<String, Note>,
    private val dateUtil: DateUtil
) : NoteCacheDataSource {
    override suspend fun insertNote(note: Note): Long {
        return when (note.id) {
            FORCE_INSERT_NOTE_EXCEPTION -> throw Exception(FORCE_INSERT_NOTE_EXCEPTION)
            FORCE_FAILURE -> -1
            else -> {
                data[note.id] = note
                1
            }
        }
    }

    override suspend fun deleteNote(id: String): Int {
        return when (id) {
            FORCE_DELETE_NOTE_EXCEPTION -> throw Exception(FORCE_DELETE_NOTE_EXCEPTION)
            else -> data.remove(id)?.let { 1 } ?: -1
        }
    }

    override suspend fun deleteNotes(notes: List<String>): Int {
        for (note in notes) {
            if (data.remove(note) == null) {
                return -1
            }
        }
        return 1
    }


    override suspend fun updateNote(
        id: String,
        newTitle: String,
        completed: Boolean,
        newColor: String,
        timestamp: String?
    ): Int {
        return when (id) {
            FORCE_UPDATE_NOTE_EXCEPTION -> throw Exception(FORCE_UPDATE_NOTE_EXCEPTION)
            else -> {
                val oldNote = data[id]
                oldNote?.let {
                    val updatedNote = Note(
                        id = oldNote.id,
                        title = newTitle,
                        completed = oldNote.completed,
                        color = newColor,
                        createdAt = oldNote.createdAt,
                        updatedAt = timestamp ?: dateUtil.getCurrentTimestamp(),
                        listId = oldNote.listId
                    )
                    data[id] = updatedNote
                    1
                } ?: -1
            }
        }
    }

    override suspend fun getAllNotes(): List<Note> = ArrayList(data.values)

    override suspend fun searchNoteById(id: String): Note? = data[id]

    override suspend fun insertNotes(notes: List<Note>): LongArray {
        val result = LongArray(notes.size)
        for ((index, note) in notes.withIndex()) {
            result[index] = 1
            data[note.id] = note
        }
        return result
    }

    companion object {
        const val FORCE_INSERT_NOTE_EXCEPTION = "Exception while inserting note"
        const val FORCE_UPDATE_NOTE_EXCEPTION = "Exception while updating note"
        const val FORCE_DELETE_NOTE_EXCEPTION = "Exception while deleting note"
        const val FORCE_FAILURE = "FORCE_FAILURE"
    }
}