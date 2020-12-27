package com.andrewbutch.noteeverything.framework.ui.notes.state

import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.StateEvent

sealed class NoteListStateEvent : StateEvent {

    class InsertNewNoteEvent(
        val title: String,
        val completed: Boolean = false,
        val color: String? = null,
        val listId: String,
        val user: User
    ) : NoteListStateEvent() {

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
        val user: User
    ) : NoteListStateEvent() {

        override fun errorInfo(): String {
            return "Error inserting new note list."
        }

        override fun eventName(): String {
            return "InsertNewNoteListEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class DeleteNoteEvent(
        val note: Note,
        val user: User
    ) : NoteListStateEvent() {
        override fun errorInfo(): String {
            return "Error deleting note"
        }

        override fun eventName(): String {
            return "DeleteNoteEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class DeleteNoteListEvent(
        val noteList: NoteList,
        val user: User
    ) : NoteListStateEvent() {
        override fun errorInfo(): String {
            return "Error deleting note list"
        }

        override fun eventName(): String {
            return "DeleteNoteListEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class DeleteMultipleNotesEvent(
        val notes: List<Note>,
        val user: User
    ) : NoteListStateEvent() {
        override fun errorInfo(): String {
            return "Error deleting selected notes"
        }

        override fun eventName(): String {
            return "DeleteMultipleNotesEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class DeleteAllNoteListsEvent(
        val user: User
    ) : NoteListStateEvent() {
        override fun errorInfo(): String {
            return "Error deleting all note lists"
        }

        override fun eventName(): String {
            return "DeleteAllNoteListsEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class GetAllNoteListsEvent(
        val user: User
    ) : NoteListStateEvent() {
        override fun errorInfo(): String {
            return "Error getting all note lists"
        }

        override fun eventName(): String {
            return "GetAllNoteListsEvent"
        }

        override fun shouldDisplayProgressBar() = true

    }

    class GetNotesByNoteListEvent(
        val noteList: NoteList,
        val user: User,
    ) : NoteListStateEvent() {
        override fun errorInfo(): String {
            return "Error getting notes by owner list"
        }

        override fun eventName(): String {
            return "GetNotesByNoteList"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class SelectNoteListEvent(
        val noteList: NoteList,
        val user: User
    ) : NoteListStateEvent() {
        override fun errorInfo(): String {
            return "Error selection note list"
        }

        override fun eventName(): String {
            return "SelectNoteListEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class ToggleNoteEvent(
        val note: Note,
        val user: User
    ) : NoteListStateEvent() {
        override fun errorInfo(): String {
            return "Error toggle note"
        }

        override fun eventName(): String {
            return "ToggleNoteEvent"
        }

        override fun shouldDisplayProgressBar(): Boolean = true
    }
}