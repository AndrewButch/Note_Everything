package com.andrewbutch.noteeverything.framework.ui.notes

import androidx.lifecycle.ViewModel
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.framework.datasource.NoteDataFactory
import javax.inject.Inject

class NotesViewModel
@Inject
constructor(val noteDataFactory: NoteDataFactory) : ViewModel() {

    fun getNotes(): List<Note> {
        return noteDataFactory.produceListOfNotes()
    }

    fun getNoteLists(): List<NoteList> {
        return noteDataFactory.produceListOfNoteList()
    }

}