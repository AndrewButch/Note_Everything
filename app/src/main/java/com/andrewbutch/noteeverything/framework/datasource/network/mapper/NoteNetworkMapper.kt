package com.andrewbutch.noteeverything.framework.datasource.network.mapper

import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.util.DateUtil
import com.andrewbutch.noteeverything.framework.datasource.network.model.NoteNetworkEntity
import javax.inject.Inject

class NoteNetworkMapper
@Inject
constructor(
    private val dateUtil: DateUtil
) {
    fun mapFromEntity(entity: NoteNetworkEntity): Note {
        return Note(
            id = entity.id,
            title = entity.title,
            completed = entity.completed,
            color = entity.color,
            createdAt = dateUtil.convertFirebaseTimestampToStringDate(entity.createdAt),
            updatedAt = dateUtil.convertFirebaseTimestampToStringDate(entity.updatedAt),
            listId = entity.listId
        )
    }


    fun mapToEntity(note: Note): NoteNetworkEntity {
        return NoteNetworkEntity(
            id = note.id,
            title = note.title,
            completed = note.completed,
            color = note.color,
            createdAt = dateUtil.convertStringDateToFirebaseTimestamp(note.createdAt),
            updatedAt = dateUtil.convertStringDateToFirebaseTimestamp(note.updatedAt),
            listId = note.listId
        )
    }

    fun mapFromEntityList(entityList: List<NoteNetworkEntity>): List<Note> {
        val noteList: ArrayList<Note> = ArrayList()
        for (entity in entityList) {
            noteList.add(mapFromEntity(entity))
        }
        return noteList
    }

    fun mapToEntityList(notes: List<Note>): List<NoteNetworkEntity> {
        val noteEntityList: ArrayList<NoteNetworkEntity> = ArrayList()
        for (note in notes) {
            noteEntityList.add(mapToEntity(note))
        }
        return noteEntityList
    }
}