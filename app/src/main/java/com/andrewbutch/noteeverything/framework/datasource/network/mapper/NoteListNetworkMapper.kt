package com.andrewbutch.noteeverything.framework.datasource.network.mapper

import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.util.DateUtil
import com.andrewbutch.noteeverything.framework.datasource.network.model.NoteListNetworkEntity
import javax.inject.Inject

class NoteListNetworkMapper
@Inject
constructor(
    private val dateUtil: DateUtil
) {

    fun mapToEntity(noteList: NoteList): NoteListNetworkEntity {
        return NoteListNetworkEntity(
            id = noteList.id,
            title = noteList.title,
            color = noteList.color,
            createdAt = dateUtil.convertStringDateToFirebaseTimestamp(noteList.createdAt),
            updatedAt = dateUtil.convertStringDateToFirebaseTimestamp(noteList.updatedAt),
        )
    }

    fun mapFromEntity(entity: NoteListNetworkEntity): NoteList {
        return NoteList(
            id = entity.id,
            title = entity.title,
            color = entity.color,
            createdAt = dateUtil.convertFirebaseTimestampToStringDate(entity.createdAt),
            updatedAt = dateUtil.convertFirebaseTimestampToStringDate(entity.updatedAt),
        )
    }


    fun mapToEntityList(noteLists: List<NoteList>): List<NoteListNetworkEntity> {
        val noteListsEntities: ArrayList<NoteListNetworkEntity> = ArrayList()
        for (note in noteLists) {
            noteListsEntities.add(mapToEntity(note))
        }
        return noteListsEntities
    }

    fun mapFromEntityList(entityList: List<NoteListNetworkEntity>): List<NoteList> {
        val noteLists: ArrayList<NoteList> = ArrayList()
        for (entity in entityList) {
            noteLists.add(mapFromEntity(entity))
        }
        return noteLists
    }
}