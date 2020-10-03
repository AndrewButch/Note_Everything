package com.andrewbutch.noteeverything.business.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Note(
    val id: String,
    var title: String,
    var completed: Boolean,
    var color: String,
    val createdAt: String,
    var updatedAt: String,
    val listId: String,
) : Parcelable {
}