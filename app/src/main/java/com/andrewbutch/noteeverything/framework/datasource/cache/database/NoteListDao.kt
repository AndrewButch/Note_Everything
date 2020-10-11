package com.andrewbutch.noteeverything.framework.datasource.cache.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andrewbutch.noteeverything.framework.datasource.cache.model.NoteListCacheEntity

@Dao
interface NoteListDao {

    @Insert
    suspend fun insertNoteList(noteList: NoteListCacheEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMultipleNoteList(noteLists: List<NoteListCacheEntity>): LongArray

    @Query("SELECT * FROM note_lists WHERE id = :id")
    suspend fun searchNoteListById(id: String): NoteListCacheEntity?

    @Query("SELECT * FROM note_lists")
    suspend fun getAllNoteLists(): List<NoteListCacheEntity>

    @Query("DELETE FROM note_lists WHERE id = :id")
    suspend fun deleteNoteList(id: String): Int

    @Query("DELETE FROM note_lists")
    suspend fun deleteAllNoteLists(): Int

    @Query(
        """ 
      UPDATE note_lists 
      SET 
      title = :title, 
      color = :color, 
      updated_at = :updatedAt 
      WHERE id = :id """
    )
    suspend fun updateNoteList(
        id: String,
        title: String,
        color: String?,
        updatedAt: String
    ): Int

}