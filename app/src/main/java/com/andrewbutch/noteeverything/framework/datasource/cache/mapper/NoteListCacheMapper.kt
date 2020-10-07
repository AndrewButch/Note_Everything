package com.andrewbutch.noteeverything.framework.datasource.cache.mapper

import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.framework.datasource.cache.model.NoteListCacheEntity

class NoteListCacheMapper {

    fun mapToEntity(noteList: NoteList): NoteListCacheEntity {
        return NoteListCacheEntity(
            id = noteList.id,
            title = noteList.title,
            color = noteList.color,
            createdAt = noteList.createdAt,
            updatedAt = noteList.updatedAt
        )
    }
    
    fun mapFromEntity(entity: NoteListCacheEntity, noteIds: ArrayList<String>): NoteList {
        return NoteList(
            id = entity.id,
            title = entity.title,
            color = entity.color,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            notes = noteIds
        )
    }
}