package com.andrewbutch.noteeverything.framework.ui.notelistdetail

import android.graphics.Color
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.state.*
import com.andrewbutch.noteeverything.business.interactors.notelistdetail.NoteListDetailInteractors
import com.andrewbutch.noteeverything.framework.ui.BaseViewModel
import com.andrewbutch.noteeverything.framework.ui.notelistdetail.state.NoteListDetailStateEvent
import com.andrewbutch.noteeverything.framework.ui.notelistdetail.state.NoteListDetailViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NoteListDetailViewModel
@Inject
constructor(
    val interactors: NoteListDetailInteractors,
    eventStore: StateEventStore,
    messageStack: MessageStack
) : BaseViewModel<NoteListDetailViewState>(eventStore, messageStack) {

    fun setStateEvent(event: StateEvent) {
        val job: Flow<DataState<NoteListDetailViewState>?> = when (event) {
            is NoteListDetailStateEvent.UpdateNoteListEvent -> {
                val noteList = getNoteList()!!

                if (noteList.id.isNotEmpty() && !checkIsTitleEmpty(noteList.title)) {
                    interactors.updateNoteList.updateNoteList(
                        noteList = noteList,
                        stateEvent = event
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
            is NoteListDetailStateEvent.DeleteNoteListEvent -> {
                val noteList = getNoteList()!!
                interactors.deleteNoteList.deleteNoteList(
                    noteList = noteList,
                    stateEvent = event
                )
            }

            is NoteListDetailStateEvent.CreateMessageDialogEvent -> {
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

    override fun handleViewState(viewState: NoteListDetailViewState) {
        // do nothing
    }


    override fun getNewViewState() = NoteListDetailViewState()

    fun setIsPendingUpdate(isPendingUpdate: Boolean) {
        val viewState = getCurrentViewStateOrNew()
        viewState.isPendingUpdate = isPendingUpdate
        setViewState(viewState)
    }

    fun isPendingUpdate() = getCurrentViewStateOrNew().isPendingUpdate

    fun setNoteListTitle(title: String) {
        if (checkIsTitleEmpty(title)) {
            return
        } else {
            val viewState = getCurrentViewStateOrNew()
            val updatedNote = viewState.noteList?.copy(title = title)
            viewState.noteList = updatedNote
            viewState.isPendingUpdate = true
            setViewState(viewState)
        }
    }

    private fun checkIsTitleEmpty(title: String): Boolean {
        return if (title.isEmpty()) {
            setStateEvent(
                NoteListDetailStateEvent.CreateMessageDialogEvent(
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

    fun setNoteList(noteList: NoteList?) {
        val viewState = getCurrentViewStateOrNew()
        viewState.noteList = noteList
        setViewState(viewState)
    }

    fun getNoteList(): NoteList? {
        return getCurrentViewStateOrNew().noteList
    }

    fun setNoteColor(color: String) {
        val viewState = getCurrentViewStateOrNew()
        val updatedNote = viewState.noteList?.copy(color = color)
        viewState.noteList = updatedNote
        viewState.isPendingUpdate = true
        setViewState(viewState)
    }

    fun getNoteListColor(): Int? {
        var intColor: Int? = null
        val noteList = getNoteList()
        noteList?.let {
            try {
                intColor = Color.parseColor(noteList.color)
            } catch (e: IllegalArgumentException) {

            }
        }
        return intColor
    }

    private fun emitStateMessageEvent(
        stateMessage: StateMessage,
        stateEvent: StateEvent
    ) = flow {
        emit(
            DataState.error<NoteListDetailViewState>(
                stateMessage = stateMessage,
                stateEvent = stateEvent
            )
        )
    }

    private companion object {
        const val EMPTY_TITLE_ERROR = "Название не может быть пустым"
        const val UPDATE_ERROR = "Ну удалось отредактировать список"
        const val COLOR_PARSE_ERROR = "Ошибка цвета, выберите заново"
    }
}