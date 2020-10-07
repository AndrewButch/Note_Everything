package com.andrewbutch.noteeverything.framework.datasource.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = NoteListCacheEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class NoteCacheEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    var title: String,
    var completed: Boolean,
    var color: String,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    var updatedAt: String,

    val listId: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NoteCacheEntity

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