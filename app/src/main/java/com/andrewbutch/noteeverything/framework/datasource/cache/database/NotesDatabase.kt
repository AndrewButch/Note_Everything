package com.andrewbutch.noteeverything.framework.datasource.cache.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.andrewbutch.noteeverything.framework.datasource.cache.model.NoteCacheEntity
import com.andrewbutch.noteeverything.framework.datasource.cache.model.NoteListCacheEntity

@Database(
    entities = [NoteListCacheEntity::class, NoteCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun noteListDao(): NoteListDao
    abstract fun noteDao(): NoteDao

    companion object {
        const val DB_NAME = "notes_db"
    }
}