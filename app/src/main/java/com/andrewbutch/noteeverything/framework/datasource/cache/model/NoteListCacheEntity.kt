package com.andrewbutch.noteeverything.framework.datasource.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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

//    @TypeConverters(NoteCacheEntityConverter::class)
//    var notes: List<NoteCacheEntity>

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

class NoteCacheEntityConverter {
    var gson = Gson()

    @TypeConverter
    fun fromTimestamp(data: String?): List<NoteCacheEntity>? {
        if (data == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<NoteCacheEntity?>?>() {}.type
        return gson.fromJson(data, type)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<NoteCacheEntity>?): String? {
        if (someObjects == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<NoteCacheEntity?>?>() {}.type
        return gson.toJson(someObjects, type)
    }
}