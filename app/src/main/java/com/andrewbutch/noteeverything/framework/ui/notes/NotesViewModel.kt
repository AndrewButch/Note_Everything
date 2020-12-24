package com.andrewbutch.noteeverything.framework.ui.notes

import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.*
import com.andrewbutch.noteeverything.business.interactors.notelist.NotesInteractors
import com.andrewbutch.noteeverything.framework.datasource.NoteDataFactory
import com.andrewbutch.noteeverything.framework.ui.BaseViewModel
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListStateEvent
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class NotesViewModel
@Inject
constructor(
    private val noteDataFactory: NoteDataFactory,
    private val notesInteractors: NotesInteractors,
    eventStore: StateEventStore,
    messageStack: MessageStack
) : BaseViewModel<NoteListViewState>(eventStore, messageStack) {

    fun setStateEvent(stateEvent: NoteListStateEvent) {
        val job: Flow<DataState<NoteListViewState>?> = when (stateEvent) {
            is NoteListStateEvent.InsertNewNoteEvent -> {
                notesInteractors.insertNewNote.insertNote(
                    title = stateEvent.title,
                    color = stateEvent.color,
                    ownerListId = stateEvent.listId,
                    user = stateEvent.user,
                    stateEvent = stateEvent
                )
            }
            is NoteListStateEvent.InsertNewNoteListEvent -> {
                notesInteractors.insertNewNoteList.insertNewNote(
                    title = stateEvent.title,
                    user = stateEvent.user,
                    stateEvent = stateEvent
                )
            }
            is NoteListStateEvent.DeleteNoteEvent ->
                notesInteractors.deleteNote.deleteNote(
                    note = stateEvent.note,
                    user = stateEvent.user,
                    stateEvent = stateEvent
                )

            is NoteListStateEvent.DeleteNoteListEvent -> {
                notesInteractors.deleteNoteList.deleteNoteList(
                    noteList = stateEvent.noteList,
                    user = stateEvent.user,
                    stateEvent = stateEvent
                )
            }
            is NoteListStateEvent.DeleteMultipleNotesEvent -> {
                notesInteractors.deleteMultipleNotes.deleteMultipleNotes(
                    notes = stateEvent.notes,
                    user = stateEvent.user,
                    stateEvent = stateEvent
                )
            }
            is NoteListStateEvent.DeleteAllNoteListsEvent -> {
                notesInteractors.clearCacheData.clear(
                    stateEvent = stateEvent
                )
            }

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
        launchJob(stateEvent, job)
    }

    override fun handleViewState(viewState: NoteListViewState) {
        viewState.newNoteList?.let {
            setNewNoteList(it)
            setSelectedList(it)
        }
        viewState.newNote?.let {
            setNewNote(it)
        }
        viewState.noteLists?.let {
            setNoteLists(it)
        }
        viewState.notes?.let {
            setNotes(it)
        }
        viewState.selectedNoteList?.let { noteList ->
            setSelectedList(noteList)
            getCurrentViewStateOrNew().user?.let {user ->
                setStateEvent(NoteListStateEvent.GetNotesByNoteListEvent(
                    noteList = noteList,
                    user = user
                ))
            }
        }
    }

    fun setSelectedList(selectedList: NoteList?) {
        val updated = getCurrentViewStateOrNew()
        updated.selectedNoteList = selectedList
        setViewState(updated)
    }

    // Set selected list from list ID (From SharedPreference)
    fun setSelectedList(selectedListId: String) {
        val updated = getCurrentViewStateOrNew()
        updated.noteLists?.let {
            for (noteList in updated.noteLists!!) {
                if (noteList.id == selectedListId) {
                    updated.selectedNoteList = noteList
                    handleViewState(updated)
                    return
                }
            }
        }
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

    private fun setNoteLists(noteLists: ArrayList<NoteList>?) {
        val updated = getCurrentViewStateOrNew()
        updated.noteLists = noteLists
        setViewState(updated)
    }

    private fun setNotes(notes: ArrayList<Note>?) {
        val updated = getCurrentViewStateOrNew()
        updated.notes = notes
        setViewState(updated)
    }

    fun setUser(user: User?) {
        val updated = getCurrentViewStateOrNew()
        updated.user = user
        setViewState(updated)
    }

    fun getSelectedNoteList() = viewState.value?.selectedNoteList

    fun reloadListItems(user: User) {
        val currentNoteList = getCurrentViewStateOrNew().selectedNoteList
        currentNoteList?.let {
            setStateEvent(NoteListStateEvent.GetNotesByNoteListEvent(currentNoteList, user))
        }
    }

    fun reloadNoteLists(user: User) {
        setStateEvent(NoteListStateEvent.GetAllNoteListsEvent(user))
    }

    override fun getNewViewState() = NoteListViewState()

    override fun emitStateMessageEvent(
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

        // Inserting notes
        val notes = noteDataFactory.produceListOfNotes()
        val ownerListId = viewState.value?.selectedNoteList?.id!!
        for (note in notes) {
            notesInteractors.insertNewNote.insertNote(
                id = null,
                title = note.title,
                color = note.color,
                ownerListId = ownerListId,
                stateEvent = NoteListStateEvent.InsertNewNoteEvent(
                    note.title,
                    note.completed,
                    note.color,
                    ownerListId,
                    user = viewState.value?.user!!
                ),
                user = viewState.value?.user!!
            )
                .onEach {
                    delay(100)
                }
                .launchIn(CoroutineScope(Dispatchers.Unconfined))

        }
    }

    fun beginPendingDelete(item: Note, user: User) {
        deleteNoteFromList(item)
        setStateEvent(NoteListStateEvent.DeleteNoteEvent(item, user))
    }

    private fun deleteNoteFromList(note: Note) {
        val updated = getCurrentViewStateOrNew()
        val list = updated.notes
        if (list?.contains(note) == true) {
            val updatedList = ArrayList(list)
            updatedList.remove(note)
            updated.notes = updatedList
            setViewState(updated)
        }
    }


    companion object {
        const val NOTE_LIST_SELECTED_MESSAGE = "Note list selected: "
    }


}