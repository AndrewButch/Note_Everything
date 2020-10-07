package com.andrewbutch.noteeverything.framework.datasource.cache.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.andrewbutch.noteeverything.framework.datasource.cache.model.NoteListCacheEntity

@Dao
interface NoteListDao {

    @Insert
    suspend fun insertNoteList(noteList: NoteListCacheEntity): Long

    @Query("SELECT * FROM note_lists WHERE id = :id")
    suspend fun searchNoteListById(id: String): NoteListCacheEntity?

    @Query("SELECT * FROM note_lists")
    suspend fun getAllNoteLists(): List<NoteListCacheEntity>

    @Delete
    suspend fun deleteNoteList(noteList: NoteListCacheEntity)

    @Query(
        """ 
      UPDATE note_lists 
      SET 
      title = :title, 
      color = :color, 
      updated_at = :updatedAt 
      WHERE id = :id """
    )
    suspend fun updateNote(
        id: String,
        title: String,
        color: String?,
        updatedAt: String
    ): Int

}