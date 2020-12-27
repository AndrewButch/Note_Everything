package com.andrewbutch.noteeverything.framework.datasource.cache.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andrewbutch.noteeverything.framework.datasource.cache.model.NoteCacheEntity

const val NOTE_ORDER_ASC: String = "asc"
const val NOTE_ORDER_DESC: String = "desc"
const val NOTE_FILTER_TITLE = "title"
const val NOTE_FILTER_DATE_CREATED = "created_at"

const val ORDER_BY_ASC_DATE_UPDATED = NOTE_FILTER_DATE_CREATED + NOTE_ORDER_ASC
const val ORDER_BY_DESC_DATE_UPDATED = NOTE_FILTER_DATE_CREATED + NOTE_ORDER_DESC
const val ORDER_BY_ASC_TITLE = NOTE_FILTER_TITLE + NOTE_ORDER_ASC
const val ORDER_BY_DESC_TITLE = NOTE_FILTER_TITLE + NOTE_ORDER_DESC


@Dao
interface NoteDao {

    @Insert
    suspend fun insertNote(note: NoteCacheEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMultipleNotes(notes: List<NoteCacheEntity>): LongArray

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun searchNoteById(id: String): NoteCacheEntity?

    @Query("SELECT * FROM notes WHERE listId = :ownerId")
    suspend fun getNotesByOwnerListId(ownerId: String): List<NoteCacheEntity>

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: String): Int

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

    @Query(
        """
        SELECT * FROM notes 
        WHERE listId = :ownerId
        ORDER BY updated_at DESC 
        """
    )
    suspend fun searchNotesOrderByDateDESC(ownerId: String): List<NoteCacheEntity>

    @Query(
        """
        SELECT * FROM notes 
        WHERE listId = :ownerId
        ORDER BY updated_at ASC 
        """
    )
    suspend fun searchNotesOrderByDateASC(ownerId: String): List<NoteCacheEntity>

    @Query(
        """
        SELECT * FROM notes 
        WHERE listId = :ownerId
        ORDER BY title DESC 
        """
    )
    suspend fun searchNotesOrderByTitleDESC(ownerId: String): List<NoteCacheEntity>

    @Query(
        """
        SELECT * FROM notes 
        WHERE listId = :ownerId
        ORDER BY title ASC 
        """
    )
    suspend fun searchNotesOrderByTitleASC(ownerId: String): List<NoteCacheEntity>
}