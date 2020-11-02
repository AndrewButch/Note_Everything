package com.andrewbutch.noteeverything.framework.ui.notedetail.state

import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.state.StateEvent

sealed class NoteDetailStateEvent : StateEvent {

    class UpdateNoteEvent(
        val note: Note
    ) : NoteDetailStateEvent() {
        override fun errorInfo(): String {
            return "Error updating note"
        }

        override fun eventName(): String {
            return "UpdateNoteEvent"
        }

        override fun shouldDisplayProgressBar() = true

    }
}