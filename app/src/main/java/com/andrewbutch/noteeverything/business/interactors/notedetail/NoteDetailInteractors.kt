package com.andrewbutch.noteeverything.business.interactors.notedetail

import com.andrewbutch.noteeverything.business.interactors.common.DeleteNote
import com.andrewbutch.noteeverything.framework.ui.notedetail.state.NoteDetailViewState

class NoteDetailInteractors
constructor(
    val updateNote: UpdateNote,
    val deleteNote: DeleteNote<NoteDetailViewState>
)