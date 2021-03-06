package com.andrewbutch.noteeverything.framework.ui.notes.state

import android.os.Parcelable
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.ViewState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoteListViewState(
    var notes: ArrayList<Note>? = null,
    var noteLists: ArrayList<NoteList>? = null,
    var newNote: Note? = null, // note that can be created with fab
    var page: Int? = null,
    var newNoteList: NoteList? = null,
    var selectedNoteList: NoteList? = null,
    var user: User? = null,
    var filter: String? = null,
    var order: String? = null,
    var enableSync: Boolean = true
) : Parcelable, ViewState {
}