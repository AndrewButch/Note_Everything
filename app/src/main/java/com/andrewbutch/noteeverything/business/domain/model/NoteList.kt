package com.andrewbutch.noteeverything.business.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoteList(
    val id: String,
    var title: String,
    var color: String,
    val created_at: String,
    var updated_at: String,
    var notes: List<Note>
) : Parcelable