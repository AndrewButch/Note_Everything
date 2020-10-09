package com.andrewbutch.noteeverything.business.domain.model

import com.andrewbutch.noteeverything.business.domain.util.DateUtil
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class NoteListFactory
@Inject
constructor(
    private val dateUtil: DateUtil,
) {
    fun createNoteList(
        id: String? = null,
        title: String,
        color: String? = null,
//        notes: ArrayList<String>? = null
    ) = NoteList(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        color = color ?: ModelConstants.COLOR,
        createdAt = dateUtil.getCurrentTimestamp(),
        updatedAt = dateUtil.getCurrentTimestamp(),
//        notes = notes ?: ArrayList()
    )

    // for testing
    fun createMultipleNoteList(noteListCount: Int) {
        val noteLists: ArrayList<NoteList> = ArrayList()
        for (i in 0 until noteListCount) {
            val noteList = createNoteList(title = UUID.randomUUID().toString())
            noteLists.add(noteList)
        }
    }

//    fun createNoteListWithNotes(notes: List<Note>): NoteList {
//        val noteList = createNoteList(title = UUID.randomUUID().toString())
//        for (i in notes.indices) {
//           noteList.notes.add(notes[i].id)
//        }
//        return noteList
//    }
}