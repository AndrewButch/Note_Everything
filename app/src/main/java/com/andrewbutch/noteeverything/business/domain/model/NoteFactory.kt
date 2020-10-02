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
    ) = Note(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        checked = checked,
        color = color ?: ModelConstants.COLOR,
        createdAt = dateUtil.getCurrentTimestamp(),
        updatedAt = dateUtil.getCurrentTimestamp()
    )

    // For testing
    fun createNoteList(noteCount: Int): List<Note> {
        val list: ArrayList<Note> = ArrayList()
        for (i in 0 until noteCount) {
            list.add(createNote(title = UUID.randomUUID().toString()))
        }
        return list
    }
}