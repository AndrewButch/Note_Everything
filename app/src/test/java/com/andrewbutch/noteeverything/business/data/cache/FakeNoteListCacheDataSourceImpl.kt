package com.andrewbutch.noteeverything.business.data.cache

import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteListCacheDataSource
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.util.DateUtil

class FakeNoteListCacheDataSourceImpl(
    private val data: HashMap<String, NoteList>,
    private val dateUtil: DateUtil
) : NoteListCacheDataSource {
    override suspend fun insertNoteList(noteList: NoteList): Long {
        return when (noteList.id) {
            FORCE_INSERT_NOTE_LIST_EXCEPTION -> throw Exception(FORCE_INSERT_NOTE_LIST_EXCEPTION)
            FORCE_FAILURE -> -1
            else -> {
                data[noteList.id] = noteList
                1
            }
        }
    }

    override suspend fun updateNoteList(
        id: String,
        newTitle: String,
        newColor: String,
        timestamp: String?
    ): Int {
        return when (id) {
            FORCE_UPDATE_NOTE_LIST_EXCEPTION -> throw Exception(FORCE_UPDATE_NOTE_LIST_EXCEPTION)
            else -> {
                val oldNoteList = data[id]
                oldNoteList?.let {
                    val updatedNote = NoteList(
                        id = oldNoteList.id,
                        title = newTitle,
                        color = newColor,
                        createdAt = oldNoteList.createdAt,
                        updatedAt = timestamp ?: dateUtil.getCurrentTimestamp(),
                    )
                    data[id] = updatedNote
                    1
                } ?: -1
            }
        }
    }

    override suspend fun deleteNoteList(id: String): Int {
        return when (id) {
            FORCE_DELETE_NOTE_LIST_EXCEPTION -> throw Exception(FORCE_DELETE_NOTE_LIST_EXCEPTION)
            else -> data.remove(id)?.let { 1 } ?: -1
        }
    }

    override suspend fun deleteAllNoteLists(): Int {
        val size = data.size
        data.clear()
        return size
    }

    override suspend fun searchNoteListById(id: String): NoteList? = data[id]

    override suspend fun getAllNoteLists(): List<NoteList> = ArrayList(data.values)

    companion object {
        const val FORCE_INSERT_NOTE_LIST_EXCEPTION = "Exception while inserting note list"
        const val FORCE_UPDATE_NOTE_LIST_EXCEPTION = "Exception while updating note list"
        const val FORCE_DELETE_NOTE_LIST_EXCEPTION = "Exception while deleting note list"
        const val FORCE_FAILURE = "FORCE_FAILURE"
    }
}