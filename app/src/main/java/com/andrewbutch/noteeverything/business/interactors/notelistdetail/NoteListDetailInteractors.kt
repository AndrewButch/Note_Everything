package com.andrewbutch.noteeverything.business.interactors.notelistdetail

import com.andrewbutch.noteeverything.business.interactors.common.DeleteNoteList
import com.andrewbutch.noteeverything.framework.ui.notelistdetail.state.NoteListDetailViewState

class NoteListDetailInteractors
constructor(
    val updateNoteList: UpdateNoteList,
    val deleteNoteList: DeleteNoteList<NoteListDetailViewState>
){
}