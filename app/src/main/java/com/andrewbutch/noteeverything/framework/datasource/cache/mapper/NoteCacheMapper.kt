package com.andrewbutch.noteeverything.framework.datasource.cache.mapper

import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.framework.datasource.cache.model.NoteCacheEntity
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class NoteCacheMapper @Inject constructor() {

    fun mapFromEntity(entity: NoteCacheEntity): Note {
        return Note(
            id = entity.id,
            title = entity.title,
            completed = entity.completed,
            color = entity.color,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            listId = entity.listId
        )
    }


    fun mapToEntity(note: Note): NoteCacheEntity {
        return NoteCacheEntity(
            id = note.id,
            title = note.title,
            completed = note.completed,
            color = note.color,
            createdAt = note.createdAt,
            updatedAt = note.updatedAt,
            listId = note.listId
        )
    }

    fun mapFromEntityList(entityList: List<NoteCacheEntity>): List<Note> {
        val noteList: ArrayList<Note> = ArrayList()
        for (entity in entityList) {
            noteList.add(mapFromEntity(entity))
        }
        return noteList
    }

    fun mapToEntityList(notes: List<Note>): List<NoteCacheEntity> {
        val noteEntityList: ArrayList<NoteCacheEntity> = ArrayList()
        for (note in notes) {
            noteEntityList.add(mapToEntity(note))
        }
        return noteEntityList
    }

}