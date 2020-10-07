package com.andrewbutch.noteeverything.framework.datasource.cache.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.andrewbutch.noteeverything.framework.datasource.cache.model.NoteCacheEntity

@Dao
interface NoteDao {

    @Insert
    suspend fun insertNote(note: NoteCacheEntity): Long

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun searchNoteById(id: String): NoteCacheEntity?

    @Query("SELECT * FROM notes")
    suspend fun getAllNotes(): List<NoteCacheEntity>

    @Delete
    suspend fun deleteNote(note: NoteCacheEntity): Int

    @Query("DELETE FROM notes WHERE id IN (:ids)")
    suspend fun deleteNotes(ids: List<String>): Int

    @Query(
        """ 
      UPDATE notes 
      SET 
      title = :title, 
      completed = :completed, 
      color = :color, 
      updated_at = :updatedAt 
      WHERE id = :id """
    )
    suspend fun updateNote(
        id: String,
        title: String,
        completed: Boolean,
        color: String?,
        updatedAt: String
    ): Int
}