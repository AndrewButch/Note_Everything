package com.andrewbutch.noteeverything.business.domain.model


data class NoteList(
    val id: String,
    var title: String,
    var color: String,
    val created_at: String,
    var updated_at: String,
    var notes: List<Note>
)