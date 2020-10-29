package com.andrewbutch.noteeverything.framework.ui.notes.state

import android.os.Parcelable
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.state.ViewState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoteListViewState(
    private var notes: List<Note>? = null,
    private var noteLists: List<NoteList>? = null,
    private var newNote: Note? = null, // note that can be created with fab
    private var page: Int? = null,
    private var newNoteList: NoteList? = null,
    private var selectedNoteList: NoteList? = null
    // filter,
    // order,
    // search,
) : Parcelable, ViewState {
}