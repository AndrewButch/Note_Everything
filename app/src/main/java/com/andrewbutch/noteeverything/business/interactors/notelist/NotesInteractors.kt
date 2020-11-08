package com.andrewbutch.noteeverything.business.interactors.notelist

import com.andrewbutch.noteeverything.business.interactors.common.DeleteNote
import com.andrewbutch.noteeverything.business.interactors.common.DeleteNoteList
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListViewState

class NotesInteractors
constructor(
    val getAllNoteLists: GetAllNoteLists,
    val getNotesByNoteList: GetNotesByNoteList,
    val deleteMultipleNotes: DeleteMultipleNotes,
    val insertNewNote: InsertNewNote,
    val insertNewNoteList: InsertNewNoteList,
    val deleteNote: DeleteNote<NoteListViewState>,
    val deleteNoteList: DeleteNoteList<NoteListViewState>
) {
}