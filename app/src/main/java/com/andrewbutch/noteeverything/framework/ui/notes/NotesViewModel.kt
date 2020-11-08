package com.andrewbutch.noteeverything.framework.ui.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.state.DataState
import com.andrewbutch.noteeverything.business.interactors.notelist.NotesInteractors
import com.andrewbutch.noteeverything.framework.datasource.NoteDataFactory
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListStateEvent
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.Flow
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

    private val _lists = MutableLiveData<List<NoteList>>()
    val lists: LiveData<List<NoteList>> = _lists

    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes


    fun setStateEvent(stateEvent: NoteListStateEvent) {
        val job: Flow<DataState<NoteListViewState>?> = when (stateEvent) {
            is NoteListStateEvent.InsertNewNoteEvent -> TODO()
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
            is NoteListStateEvent.GetNotesByNoteListEvent -> TODO()
            is NoteListStateEvent.SelectNoteListEvent -> {
                notesInteractors.getNotesByNoteList.getNotesByNoteList(
                    stateEvent = stateEvent,
                    ownerListId = stateEvent.noteList.id
                )
            }
        }

        job
            .onEach {
                it?.data?.let { viewState ->
                    withContext(Main) {
                        handleNewViewState(viewState)
                    }
                }
            }
            .launchIn(CoroutineScope(IO))
    }

    private fun handleNewViewState(viewState: NoteListViewState?) {
        viewState?.newNoteList?.let {
            // TODO(Handle note list inserting)
        }
        viewState?.newNote?.let {
            // TODO(Handle note inserting)
        }
        viewState?.noteLists?.let {
            _lists.value = it
        }
        viewState?.notes?.let {
            _notes.value = it
        }
        viewState?.selectedNoteList?.let {
        }
        viewState?.page?.let {
            // TODO(handle page in long list of notes)
        }
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
        const val TAG = "NotesViewModel"
    }

}