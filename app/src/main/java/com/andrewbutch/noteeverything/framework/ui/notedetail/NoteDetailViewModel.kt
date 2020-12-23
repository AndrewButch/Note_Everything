package com.andrewbutch.noteeverything.framework.ui.notedetail

import android.graphics.Color
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.state.*
import com.andrewbutch.noteeverything.business.interactors.notedetail.NoteDetailInteractors
import com.andrewbutch.noteeverything.framework.ui.BaseViewModel
import com.andrewbutch.noteeverything.framework.ui.notedetail.state.NoteDetailStateEvent
import com.andrewbutch.noteeverything.framework.ui.notedetail.state.NoteDetailViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NoteDetailViewModel
@Inject
constructor(
    private val interactors: NoteDetailInteractors,
    eventStore: StateEventStore,
    messageStack: MessageStack
) : BaseViewModel<NoteDetailViewState>(eventStore, messageStack) {

    fun setStateEvent(event: StateEvent) {
        val job: Flow<DataState<NoteDetailViewState>?> = when (event) {
            is NoteDetailStateEvent.UpdateNoteEvent -> {
                val note = getNote()!!

                if (note.id.isNotEmpty() && !checkIsTitleEmpty(note.title)) {
                    interactors.updateNote.updateNote(
                        note = note,
                        stateEvent = event,
                        user = event.user
                    )
                } else {
                    emitStateMessageEvent(
                        stateMessage = StateMessage(
                            message = UPDATE_ERROR,
                            uiComponentType = UIComponentType.Dialog,
                            messageType = MessageType.Error
                        ),
                        stateEvent = event
                    )
                }

            }
            is NoteDetailStateEvent.DeleteNoteEvent -> {
                val note = getNote()!!
                interactors.deleteNote.deleteNote(
                    note = note,
                    stateEvent = event,
                    user = event.user
                )
            }

            is NoteDetailStateEvent.CreateMessageDialogEvent -> {
                emitStateMessageEvent(
                    stateMessage = event.stateMessage,
                    stateEvent = event
                )
            }
            else -> {
                emitStateMessageEvent(
                    stateMessage = StateMessage(
                        message = UPDATE_ERROR,
                        uiComponentType = UIComponentType.Dialog,
                        messageType = MessageType.Error
                    ),
                    stateEvent = event
                )
            }
        }
        launchJob(event, job)
//        job
//            .onEach {
//                withContext(Dispatchers.Main) {
//                    it?.data?.let { viewState ->
//                        handleViewState(viewState)
//                    }
//                    it?.stateMessage?.let { stateMessage ->
//                        handleStateMessage(stateMessage)
//                    }
//                }
//            }
//            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    override fun handleViewState(viewState: NoteDetailViewState) {
        // do nothing
    }


    fun setNote(note: Note?) {
        val viewState = getCurrentViewStateOrNew()
        viewState.note = note
        setViewState(viewState)
    }

    fun setNoteTitle(title: String) {
        if (checkIsTitleEmpty(title)) {
            return
        } else {
            val viewState = getCurrentViewStateOrNew()
            val updatedNote = viewState.note?.copy(title = title)
            viewState.note = updatedNote
            viewState.isPendingUpdate = true
            setViewState(viewState)
        }
    }

    private fun checkIsTitleEmpty(title: String): Boolean {
        return if (title.isEmpty()) {
            setStateEvent(
                NoteDetailStateEvent.CreateMessageDialogEvent(
                    stateMessage = StateMessage(
                        message = EMPTY_TITLE_ERROR,
                        uiComponentType = UIComponentType.Dialog,
                        messageType = MessageType.Info
                    )
                )
            )
            true
        } else {
            false
        }
    }

    fun setNoteColor(color: String) {
        val viewState = getCurrentViewStateOrNew()
        val updatedNote = viewState.note?.copy(color = color)
        viewState.note = updatedNote
        viewState.isPendingUpdate = true
        setViewState(viewState)
    }

    fun setNoteCompleted(completed: Boolean) {
        val viewState = getCurrentViewStateOrNew()
        val updatedNote = viewState.note?.copy(completed = completed)
        viewState.note = updatedNote
        viewState.isPendingUpdate = true
        setViewState(viewState)
    }

    fun setIsPendingUpdate(isPendingUpdate: Boolean) {
        val viewState = getCurrentViewStateOrNew()
        viewState.isPendingUpdate = isPendingUpdate
        setViewState(viewState)
    }

    fun getNote(): Note? {
        return getCurrentViewStateOrNew().note
    }

    fun getNoteColor(): Int? {
        var intColor: Int? = null
        val note = getNote()
        note?.let {
            try {
                intColor = Color.parseColor(note.color)
            }catch (e: IllegalArgumentException) {

            }
        }
        return intColor
    }



    fun isPendingUpdate() = getCurrentViewStateOrNew().isPendingUpdate

    override fun getNewViewState() = NoteDetailViewState()

    private fun emitStateMessageEvent(
        stateMessage: StateMessage,
        stateEvent: StateEvent
    ) = flow {
        emit(
            DataState.error<NoteDetailViewState>(
                stateMessage = stateMessage,
                stateEvent = stateEvent
            )
        )
    }

    private companion object {
        const val EMPTY_TITLE_ERROR = "Название не может быть пустым"
        const val UPDATE_ERROR = "Ну удалось отредактировать заметку"
        const val COLOR_PARSE_ERROR = "Ошибка цвета, выберите заново"
    }
}