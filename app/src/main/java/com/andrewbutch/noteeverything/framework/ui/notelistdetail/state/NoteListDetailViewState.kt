package com.andrewbutch.noteeverything.framework.ui.notelistdetail.state

import android.os.Parcelable
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.state.ViewState
import kotlinx.android.parcel.Parcelize

@Parcelize
class NoteListDetailViewState(
    var noteList: NoteList? = null
) : Parcelable, ViewState
