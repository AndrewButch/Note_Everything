package com.andrewbutch.noteeverything.framework.ui.notedetail.state

import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.state.StateEvent
import com.andrewbutch.noteeverything.business.domain.state.StateMessage

sealed class NoteDetailStateEvent : StateEvent {

    class UpdateNoteEvent(
    ) : NoteDetailStateEvent() {
        override fun errorInfo(): String {
            return "Error updating note"
        }

        override fun eventName(): String {
            return "UpdateNoteEvent"
        }

        override fun shouldDisplayProgressBar() = true

    }

    class DeleteNoteEvent(
        val note: Note
    ) : NoteDetailStateEvent() {
        override fun errorInfo(): String {
            return "Error deleting note"
        }

        override fun eventName(): String {
            return "DeleteNoteEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class CreateMessageDialogEvent(
        val stateMessage: StateMessage
    ) : NoteDetailStateEvent() {
        override fun errorInfo(): String {
            return "Error creating message dialog"
        }

        override fun eventName(): String {
            return "CreateMessageDialogEvent"
        }

        override fun shouldDisplayProgressBar() = false
    }


}