package com.andrewbutch.noteeverything.business.interactors.notedetail

import com.andrewbutch.noteeverything.business.interactors.common.DeleteNote
import com.andrewbutch.noteeverything.framework.ui.notedetail.state.NoteDetailViewState
import javax.inject.Inject

class NoteDetailInteractors
@Inject
constructor(
    val updateNote: UpdateNote,
    val deleteNote: DeleteNote<NoteDetailViewState>
)