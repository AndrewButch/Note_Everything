package com.andrewbutch.noteeverything.business.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Note(
    val id: String,
    val title: String,
    val checked: Boolean,
    val color: String,
    val createdAt: String,
    val updatedAt: String,
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Note

        if (id != other.id) return false
        if (title != other.title) return false
        if (checked != other.checked) return false
        if (color != other.color) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + checked.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }
}