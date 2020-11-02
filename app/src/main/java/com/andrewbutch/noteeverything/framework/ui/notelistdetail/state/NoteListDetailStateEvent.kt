package com.andrewbutch.noteeverything.framework.ui.notelistdetail.state

import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.state.StateEvent

sealed class NoteListDetailStateEvent : StateEvent {

    class UpdateNoteListStateEvent(val noteList: NoteList) : NoteListDetailStateEvent() {
        override fun errorInfo(): String {
            return "Error updating noteList"
        }

        override fun eventName(): String {
            return "UpdateNoteListStateEvent"
        }

        override fun shouldDisplayProgressBar() = true

    }
}