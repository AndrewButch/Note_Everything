package com.andrewbutch.noteeverything.framework.ui.notes

import android.content.SharedPreferences
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.*
import com.andrewbutch.noteeverything.business.interactors.notelist.NotesInteractors
import com.andrewbutch.noteeverything.framework.datasource.NoteDataFactory
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NOTE_FILTER_DATE_CREATED
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NOTE_ORDER_DESC
import com.andrewbutch.noteeverything.framework.ui.BaseViewModel
import com.andrewbutch.noteeverything.framework.ui.PreferenceKeys.Companion.FILTER_ORDER
import com.andrewbutch.noteeverything.framework.ui.PreferenceKeys.Companion.FILTER_TYPE
import com.andrewbutch.noteeverything.framework.ui.PreferenceKeys.Companion.SELECTED_NOTE
import com.andrewbutch.noteeverything.framework.ui.PreferenceKeys.Companion.SYNC
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListStateEvent
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListViewState
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class NotesViewModel
@Inject
constructor(
    private val noteDataFactory: NoteDataFactory,
    private val notesInteractors: NotesInteractors,
    private val sharedPreferences: SharedPreferences,
    eventStore: StateEventStore,
    messageStack: MessageStack,
) : BaseViewModel<NoteListViewState>(eventStore, messageStack) {

    init {
        initNoteFilterType()
        initNoteFilterOrder()
        initSyncOption()
    }

    private fun initNoteFilterType() {
        setNoteFilter(sharedPreferences.getString(FILTER_TYPE, NOTE_FILTER_DATE_CREATED))
    }

    private fun initNoteFilterOrder() {
        setNoteOrder(sharedPreferences.getString(FILTER_ORDER, NOTE_ORDER_DESC))
    }

    private fun initSyncOption() {
        setSyncOption(sharedPreferences.getBoolean(SYNC, true))
    }

    // call after receive all lists
    fun initSelectedNoteList() {
        setSelectedNoteList(
            sharedPreferences.getString(SELECTED_NOTE, null)
        )
    }

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
                    ownerListId = stateEvent.noteList.id,
                    filterAndOrder = getFilter() + getOrder()
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
            is NoteListStateEvent.ToggleNoteEvent -> {
                notesInteractors.updateNote.updateNote(
                    note = stateEvent.note,
                    user = stateEvent.user,
                    stateEvent = stateEvent
                )
            }
        }
        launchJob(stateEvent, job)
    }

    override fun handleViewState(viewState: NoteListViewState) {
        viewState.newNoteList?.let {
            setNewNoteList(it)
            setSelectedNoteList(it)
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
            setSelectedNoteList(noteList)
            getCurrentViewStateOrNew().user?.let { user ->
                setStateEvent(
                    NoteListStateEvent.GetNotesByNoteListEvent(
                        noteList = noteList,
                        user = user
                    )
                )
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

    fun setSelectedNoteList(selectedList: NoteList?) {
        val updated = getCurrentViewStateOrNew()
        updated.selectedNoteList = selectedList
        setViewState(updated)
    }

    // Set selected list by list ID (extracted from SharedPreference)
    private fun setSelectedNoteList(selectedListId: String?) {
        if (selectedListId == null) {
            return
        }
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

    // Get current user or invalid user with id = -1
    private fun getUser(): User {
        return getCurrentViewStateOrNew().user ?: User(
            id = "-1",
            displayName = "Invalid user",
            email = "Invalid email"
        )
    }

    fun getSelectedNoteList() = viewState.value?.selectedNoteList

    fun reloadNotes(user: User) {
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


    fun beginPendingNoteDelete(item: Note, user: User) {
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

    // Update Note "completed" field when user toggle checkmark
    fun beginPendingNoteUpdate(note: Note) {
        val updatedNote = updateNoteInStateEvent(note)
        updatedNote?.let {
            setStateEvent(
                NoteListStateEvent.ToggleNoteEvent(
                    updatedNote,
                    getCurrentViewStateOrNew().user!!
                )
            )
        }
    }

    private fun updateNoteInStateEvent(note: Note): Note? {
        val updated = getCurrentViewStateOrNew()
        val list = updated.notes
        if (list?.contains(note) == true) {
            val updatedList = ArrayList(list)
            val index = updatedList.indexOf(note)
            val noteCopy = note.copy(completed = !note.completed)
            updatedList.removeAt(index)
            updatedList.add(index, noteCopy)
            updated.notes = updatedList
            setViewState(updated)
            return noteCopy
        }
        return null
    }

    fun getFilter(): String {
        return getCurrentViewStateOrNew().filter ?: NOTE_FILTER_DATE_CREATED
    }

    fun getOrder(): String {
        return getCurrentViewStateOrNew().order ?: NOTE_ORDER_DESC
    }

    fun getSyncOption(): Boolean {
        return getCurrentViewStateOrNew().enableSync
    }

    fun setNoteFilter(filter: String?) {
        val updated = getCurrentViewStateOrNew()
        updated.filter = filter
        setViewState(updated)
    }

    fun setNoteOrder(order: String?) {
        val updated = getCurrentViewStateOrNew()
        updated.order = order
        setViewState(updated)
    }

    fun setSyncOption(enableSync: Boolean) {
        val updated = getCurrentViewStateOrNew()
        updated.enableSync = enableSync
        setViewState(updated)
    }



    fun saveSharedPreference() {
        val editor = sharedPreferences.edit()
        editor.putString(SELECTED_NOTE, getSelectedNoteList()?.id)
        editor.putBoolean(SYNC, getSyncOption())
        editor.putString(FILTER_TYPE, getFilter())
        editor.putString(FILTER_ORDER, getOrder())
        editor.apply()
    }

    companion object {
        const val NOTE_LIST_SELECTED_MESSAGE = "Note list selected: "
    }


    /** ONLY FOR TEST */
    fun insertTestData() {
        // Inserting notes
        val notes = noteDataFactory.produceListOfNotes()
        val ownerListId = viewState.value?.selectedNoteList?.id!!
        CoroutineScope(IO).launch {
            val jobs: ArrayList<Job> = ArrayList()
            for ((index, note) in notes.withIndex()) {
                val job = notesInteractors.insertNewNote.insertNote(
                    id = null,
                    title = "Note $index, list ${getSelectedNoteList()?.title}",
                    color = note.color,
                    ownerListId = ownerListId,
                    stateEvent = NoteListStateEvent.InsertNewNoteEvent(
                        note.title,
                        note.completed,
                        note.color,
                        ownerListId,
                        user = getUser()
                    ),
                    user = viewState.value?.user!!
                )
                    .launchIn(CoroutineScope(IO))
                jobs.add(job)
            }

            withContext(Main) {
                joinAll(*jobs.toTypedArray())
                reloadNotes(getUser())
            }
        }
    }


}