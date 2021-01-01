package com.andrewbutch.noteeverything.business.interactors.notes

import com.andrewbutch.noteeverything.business.data.cache.CacheResultHandler
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.data.util.safeCacheCall
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.*
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteMultipleNotes
constructor(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
) {

    fun deleteMultipleNotes(
        notes: List<Note>,
        user: User,
        stateEvent: StateEvent,
    ): Flow<DataState<NoteListViewState>?> = flow {
        var deletingError = false // set true if error while delete

        val successfullyDeleted = ArrayList<Note>()

        for (note in notes) {
            val cacheResult = safeCacheCall(Dispatchers.IO) {
                noteCacheDataSource.deleteNote(note.id)
            }

            val cacheResponse = object : CacheResultHandler<NoteListViewState, Int>(
                result = cacheResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultValue: Int): DataState<NoteListViewState>? {
                    if (resultValue < 0) {
                        // error
                    } else {
                        successfullyDeleted.add(note)
                    }
                    return null
                }
            }.getResult()

            // check for random errors
            cacheResponse?.stateMessage?.message?.let {
                deletingError = true
            }
        }

        if (deletingError) {
            emit(
                DataState.data<NoteListViewState>(
                    stateMessage = StateMessage(
                        message = DELETE_MULTIPLE_NOTES_FAILURE,
                        uiComponentType = UIComponentType.Dialog,
                        messageType = MessageType.Success
                    ),
                    data = null,
                    stateEvent = stateEvent
                )
            )
        } else {
            emit(
                DataState.data<NoteListViewState>(
                    stateMessage = StateMessage(
                        message = DELETE_MULTIPLE_NOTES_SUCCESS,
                        uiComponentType = UIComponentType.Toast,
                        messageType = MessageType.Success
                    ),
                    data = null,
                    stateEvent = stateEvent
                )
            )
        }
        updateNetwork(successfullyDeleted, user)
    }

    private suspend fun updateNetwork(notes: List<Note>, user: User) {
        for (note in notes) {
            safeCacheCall(Dispatchers.IO) {
                noteNetworkDataSource.deleteNote(note, user)
            }
        }
    }


    companion object {
        const val DELETE_MULTIPLE_NOTES_SUCCESS = "Successfully deleted multiple notes"
        const val DELETE_MULTIPLE_NOTES_FAILURE = "Not all notes was deleted"
    }
}