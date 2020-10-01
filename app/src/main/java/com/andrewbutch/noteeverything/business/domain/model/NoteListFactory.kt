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
    private val noteFactory: NoteFactory
) {
    fun createNoteList(
        id: String? = null,
        title: String,
        color: String = ModelConstants.COLOR,
        notes: List<Note>? = null
    ) = NoteList(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        color = color,
        created_at = dateUtil.getCurrentTimestamp(),
        updated_at = dateUtil.getCurrentTimestamp(),
        notes = notes ?: ArrayList()
    )

    // for testing
    fun createMultipleNoteList(noteListCount: Int) {
        val noteLists: ArrayList<NoteList> = ArrayList()
        for (i in 0 until noteListCount) {
            noteLists.add(
                createNoteList(
                    title = UUID.randomUUID().toString(),
                    notes = noteFactory.createNoteList(noteListCount)
                )
            )
        }
    }
}