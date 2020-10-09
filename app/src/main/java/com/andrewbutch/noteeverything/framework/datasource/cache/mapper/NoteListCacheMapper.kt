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
            updatedAt = noteList.updatedAt,
        )
    }
    
    fun mapFromEntity(entity: NoteListCacheEntity): NoteList {
        return NoteList(
            id = entity.id,
            title = entity.title,
            color = entity.color,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
//            notes = noteIds
        )
    }

    // Can`t map notes
//
//    fun mapToEntityList(noteLists: List<NoteList> ): List<NoteListCacheEntity> {
//        val noteListsEntities: ArrayList<NoteListCacheEntity> = ArrayList()
//        for (note in noteLists) {
//            noteListsEntities.add(mapToEntity(note, ArrayList()))
//        }
//        return noteListsEntities
//    }

    fun mapFromEntityList(entityList: List<NoteListCacheEntity>): List<NoteList> {
        val noteLists: ArrayList<NoteList> = ArrayList()
        for (entity in entityList) {
//            val noteIds: ArrayList<String> = ArrayList()
//            for (note in entity.notes ) {
//                noteIds.add(note.id)
//            }
            noteLists.add(mapFromEntity(entity))
        }
        return noteLists
    }


}