package com.andrewbutch.noteeverything.framework.ui.notes.state

import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.state.StateEvent

sealed class NoteListStateEvent : StateEvent {

    class InsertNewNoteEvent(
        val title: String,
        val completed: Boolean,
        val color: String,
        val listId: String
    ): NoteListStateEvent() {

        override fun errorInfo(): String {
            return "Error inserting new note."
        }

        override fun eventName(): String {
            return "InsertNewNoteEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class InsertNewNoteListEvent(
        val title: String,
        val color: String,
    ): NoteListStateEvent() {

        override fun errorInfo(): String {
            return "Error inserting new note list."
        }

        override fun eventName(): String {
            return "InsertNewNoteListEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class DeleteNoteEvent(
        val note: Note
    ): NoteListStateEvent() {
        override fun errorInfo(): String {
            return "Error deleting note"
        }

        override fun eventName(): String {
            return "DeleteNoteEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }
}