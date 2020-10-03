package com.andrewbutch.noteeverything.business.domain.model

import com.andrewbutch.noteeverything.business.domain.util.DateUtil
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class NoteFactory
@Inject
constructor(
    private val dateUtil: DateUtil
) {

    fun createNote(
        id: String? = null,
        title: String,
        checked: Boolean = false,
        color: String? = null,
        listId: String
    ) = Note(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        completed = checked,
        color = color ?: ModelConstants.COLOR,
        createdAt = dateUtil.getCurrentTimestamp(),
        updatedAt = dateUtil.getCurrentTimestamp(),
        listId = listId
    )

    // For testing
    fun createNoteList(noteCount: Int, ownerListId: String): List<Note> {
        val list: ArrayList<Note> = ArrayList()
        for (i in 0 until noteCount) {
            list.add(createNote(title = UUID.randomUUID().toString(), listId = ownerListId))
        }
        return list
    }
}