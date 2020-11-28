package com.andrewbutch.noteeverything.framework.ui.notelistdetail

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.state.*
import com.andrewbutch.noteeverything.business.interactors.notelistdetail.NoteListDetailInteractors
import com.andrewbutch.noteeverything.framework.ui.notedetail.state.NoteDetailStateEvent
import com.andrewbutch.noteeverything.framework.ui.notelistdetail.state.NoteListDetailViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteListDetailViewModel
@Inject
constructor(
    val interactors: NoteListDetailInteractors
) : ViewModel() {
    private var _viewState: MutableLiveData<NoteListDetailViewState> = MutableLiveData()
    val viewState: LiveData<NoteListDetailViewState>
        get() = _viewState

    fun setStateEvent(event: StateEvent) {
        val job: Flow<DataState<NoteListDetailViewState>?> = when (event) {
            is NoteDetailStateEvent.UpdateNoteEvent -> {
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
            is NoteDetailStateEvent.DeleteNoteEvent -> {
                val noteList = getNoteList()!!
                interactors.deleteNoteList.deleteNoteList(
                    noteList = noteList,
                    stateEvent = event
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
        job
            .onEach {
                withContext(Dispatchers.Main) {
                    it?.data?.let { viewState ->
                        handleViewState(viewState)
                    }
                    it?.stateMessage?.let { stateMessage ->
                        handleStateMessage(stateMessage)
                    }
                }
            }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    private fun handleStateMessage(stateMessage: StateMessage) {
        // todo delegate to job manager
    }

    private fun handleViewState(viewState: NoteListDetailViewState) {
        // todo delegate to job manager
    }


    private fun getCurrentViewStateOrNew() = _viewState.value ?: getNewViewState()

    private fun getNewViewState() = NoteListDetailViewState()

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

    private fun setViewState(viewState: NoteListDetailViewState) {
        _viewState.value = viewState
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