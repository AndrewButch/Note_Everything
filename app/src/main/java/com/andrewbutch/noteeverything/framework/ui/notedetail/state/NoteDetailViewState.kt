package com.andrewbutch.noteeverything.framework.ui.notedetail.state

import android.os.Parcelable
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.state.ViewState
import kotlinx.android.parcel.Parcelize

@Parcelize
class NoteDetailViewState(
    var note: Note? = null,
    var isPendingUpdate: Boolean = false
) : Parcelable, ViewState