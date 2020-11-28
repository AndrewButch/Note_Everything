package com.andrewbutch.noteeverything.framework.ui.notes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.state.*
import com.andrewbutch.noteeverything.business.interactors.notelist.NotesInteractors
import com.andrewbutch.noteeverything.framework.datasource.NoteDataFactory
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListStateEvent
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotesViewModel
@Inject
constructor(
    private val noteDataFactory: NoteDataFactory,
    private val notesInteractors: NotesInteractors
) : ViewModel() {

    private val _viewState = MutableLiveData<NoteListViewState>()
    val viewState: LiveData<NoteListViewState>
        get() = _viewState

    init {
        Log.d(TAG, "INIT")
    }

    fun setStateEvent(stateEvent: NoteListStateEvent) {
        val job: Flow<DataState<NoteListViewState>?> = when (stateEvent) {
            is NoteListStateEvent.InsertNewNoteEvent -> {
                notesInteractors.insertNewNote.insertNote(
                    title = stateEvent.title,
                    color = stateEvent.color,
                    ownerListId = stateEvent.listId,
                    stateEvent = stateEvent
                )
            }
            is NoteListStateEvent.InsertNewNoteListEvent -> {
                notesInteractors.insertNewNoteList.insertNewNote(
                    title = stateEvent.title,
                    color = stateEvent.color,
                    stateEvent = stateEvent
                )
            }
            is NoteListStateEvent.DeleteNoteEvent -> TODO()
            is NoteListStateEvent.DeleteNoteListEvent -> TODO()
            is NoteListStateEvent.DeleteMultipleNotesEvent -> TODO()
            is NoteListStateEvent.GetAllNoteListsEvent -> {
                notesInteractors.getAllNoteLists.getAllNoteLists(stateEvent)
            }
            is NoteListStateEvent.GetNotesByNoteListEvent -> {
                notesInteractors.getNotesByNoteList.getNotesByNoteList(
                    stateEvent = stateEvent,
                    ownerListId = stateEvent.noteList.id
                )
            }
            is NoteListStateEvent.SelectNoteListEvent -> {
                // todo save selected list in
                setSelectedList(stateEvent.noteList)
//                notesInteractors.getNotesByNoteList.getNotesByNoteList(
//                    stateEvent = stateEvent,
//                    ownerListId = stateEvent.noteList.id
//                )
                emitStateMessageEvent(
                    stateMessage = StateMessage(
                        message = "$NOTE_LIST_SELECTED_MESSAGE ${stateEvent.noteList.title}",
                        uiComponentType = UIComponentType.None,
                        messageType = MessageType.Info
                    ),
                    data = NoteListViewState(selectedNoteList = stateEvent.noteList),
                    stateEvent = stateEvent
                )
            }
        }

        job
            .onEach {
                withContext(Main) {
                    it?.data?.let { viewState ->
                        handleViewState(viewState)
                    }
                    it?.stateMessage?.let { stateMessage ->
                        handleStateMessage(stateMessage)
                    }
                }
            }
            .launchIn(CoroutineScope(IO))
    }

    private fun handleStateMessage(stateMessage: StateMessage) {

    }

    private fun handleViewState(viewState: NoteListViewState?) {
        viewState?.newNoteList?.let {
            // TODO(Handle note list inserting)
        }
        viewState?.newNote?.let {
            setNewNote(it)
        }
        viewState?.noteLists?.let {
            setNoteLists(it)
        }
        viewState?.notes?.let {
            setNotes(it)
        }
        viewState?.selectedNoteList?.let {
            setStateEvent(NoteListStateEvent.GetNotesByNoteListEvent(it))
        }
        viewState?.page?.let {
            // TODO(handle page in long list of notes)
        }
    }

    fun setSelectedList(selectedList: NoteList) {
        val updated = getCurrentViewStateOrNew()
        updated.selectedNoteList = selectedList
        setViewState(updated)
    }

    fun setNewNote(note: Note?) {
        val updated = getCurrentViewStateOrNew()
        updated.newNote = note
        setViewState(updated)
    }

    fun setNewNoteList(noteList: NoteList?) {
        val updated = getCurrentViewStateOrNew()
        updated.newNoteList = noteList
        setViewState(updated)
    }

    fun setNoteLists(noteLists: List<NoteList>?) {
        val updated = getCurrentViewStateOrNew()
        updated.noteLists = noteLists
        setViewState(updated)
    }

    fun setNotes(notes: List<Note>?) {
        val updated = getCurrentViewStateOrNew()
        updated.notes = notes
        setViewState(updated)
    }

    private fun setViewState(viewState: NoteListViewState) {
        _viewState.value = viewState
    }

    fun reloadListItems() {
        val currentNoteList = getCurrentViewStateOrNew().selectedNoteList
        currentNoteList?.let {
            setStateEvent(NoteListStateEvent.SelectNoteListEvent(currentNoteList))
        }
    }

    fun reloadNoteLists() {
        setStateEvent(NoteListStateEvent.GetAllNoteListsEvent())
    }

    private fun getCurrentViewStateOrNew() = _viewState.value ?: getNewViewState()

    private fun getNewViewState() = NoteListViewState()

    private fun emitStateMessageEvent(
        stateMessage: StateMessage,
        data: NoteListViewState,
        stateEvent: StateEvent
    ) = flow {
        emit(
            DataState.data<NoteListViewState>(
                stateMessage = stateMessage,
                data = data,
                stateEvent = stateEvent
            )
        )
    }

    /** ONLY FOR INSERT ONCE */
    fun insertTestData() {
//        // Inserting lists
//        val lists = noteDataFactory.produceListOfNoteList()
//        for (list in lists) {
//            notesInteractors.insertNewNoteList.insertNewNote(
//                id = list.id,
//                title = list.title,
//                color = list.color,
//                stateEvent = NoteListStateEvent.InsertNewNoteListEvent(list.title, list.color)
//            )
//                .launchIn(CoroutineScope(IO))
//        }

//        // Inserting notes
//        val notes = noteDataFactory.produceListOfNotes()
//        for (note in notes) {
//            notesInteractors.insertNewNote.insertNote(
//                id = note.id,
//                title = note.title,
//                color = note.color,
//                ownerListId = note.listId,
//                stateEvent = NoteListStateEvent.InsertNewNoteEvent(
//                    note.title,
//                    note.completed,
//                    note.color,
//                    note.listId
//                )
//            )
//                .launchIn(CoroutineScope(IO))
//        }
    }


    companion object {
        const val TAG = "!@#NotesViewModel"
        const val NOTE_LIST_SELECTED_MESSAGE = "Note list selected: "
    }

}