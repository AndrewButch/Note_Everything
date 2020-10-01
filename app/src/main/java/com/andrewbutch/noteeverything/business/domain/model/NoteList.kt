package com.andrewbutch.noteeverything.business.domain.model


data class NoteList(
    val id: String,
    val title: String,
    val color: String,
    val created_at: String,
    val updated_at: String,
    val notes: List<Note>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NoteList

        if (id != other.id) return false
        if (title != other.title) return false
        if (color != other.color) return false
        if (created_at != other.created_at) return false
        if (notes != other.notes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + created_at.hashCode()
        result = 31 * result + notes.hashCode()
        return result
    }
}