package com.andrewbutch.noteeverything.framework.datasource.network.model

import com.google.firebase.Timestamp


data class NoteNetworkEntity(
    val id: String,

    val title: String,

    val completed: Boolean,

    val color: String,

    val createdAt: Timestamp = Timestamp.now(),

    val updatedAt: Timestamp = Timestamp.now(),

    val listId: String,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NoteNetworkEntity

        if (id != other.id) return false
        if (title != other.title) return false
        if (completed != other.completed) return false
        if (color != other.color) return false
        if (createdAt != other.createdAt) return false
        if (listId != other.listId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + completed.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + listId.hashCode()
        return result
    }
}