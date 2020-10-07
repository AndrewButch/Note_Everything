package com.andrewbutch.noteeverything.framework.datasource.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_lists")
data class NoteListCacheEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    var title: String,
    var color: String,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    var updatedAt: String,

    var notes: List<NoteCacheEntity>

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NoteListCacheEntity

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