package com.andrewbutch.noteeverything.framework.datasource.network.model

import com.google.firebase.Timestamp

data class NoteListNetworkEntity(

    val id: String,

    var title: String,

    var color: String,

    val createdAt: Timestamp = Timestamp.now(),

    val updatedAt: Timestamp = Timestamp.now(),

    )
{
    constructor(): this(
        "",
        "",
        "",
        Timestamp.now(),
        Timestamp.now()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NoteListNetworkEntity

        if (id != other.id) return false
        if (title != other.title) return false
        if (color != other.color) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }
}