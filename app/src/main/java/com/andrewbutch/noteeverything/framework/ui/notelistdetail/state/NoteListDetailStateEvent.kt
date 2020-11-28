package com.andrewbutch.noteeverything.framework.ui.notelistdetail.state

import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.state.StateEvent
import com.andrewbutch.noteeverything.business.domain.state.StateMessage

sealed class NoteListDetailStateEvent : StateEvent {

    class UpdateNoteListEvent() : NoteListDetailStateEvent() {
        override fun errorInfo(): String {
            return "Error updating noteList"
        }

        override fun eventName(): String {
            return "UpdateNoteListEvent"
        }

        override fun shouldDisplayProgressBar() = true

    }

    class DeleteNoteListEvent(
        val noteList: NoteList
    ) : NoteListDetailStateEvent() {
        override fun errorInfo(): String {
            return "Error deleting note list"
        }

        override fun eventName(): String {
            return "DeleteNoteListEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class CreateMessageDialogEvent(
        val stateMessage: StateMessage
    ) : NoteListDetailStateEvent() {
        override fun errorInfo(): String {
            return "Error creating message dialog"
        }

        override fun eventName(): String {
            return "CreateMessageDialogEvent"
        }

        override fun shouldDisplayProgressBar() = false
    }
}