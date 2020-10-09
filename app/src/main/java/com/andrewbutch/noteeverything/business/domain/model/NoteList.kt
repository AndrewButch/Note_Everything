package com.andrewbutch.noteeverything.business.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoteList(
    val id: String,
    var title: String,
    var color: String,
    val createdAt: String,
    var updatedAt: String,
//    var notes: List<String>
) : Parcelable