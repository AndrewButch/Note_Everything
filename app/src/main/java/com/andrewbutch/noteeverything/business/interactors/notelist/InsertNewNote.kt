package com.andrewbutch.noteeverything.business.interactors.notelist

import com.andrewbutch.noteeverything.business.data.cache.CacheResultHandler
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.data.util.safeCacheCall
import com.andrewbutch.noteeverything.business.data.util.safeNetworkCall
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteFactory
import com.andrewbutch.noteeverything.business.domain.state.*
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InsertNewNote
constructor(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource,
    private val noteFactory: NoteFactory
) {

    fun insertNote(
        id: String? = null,
        title: String,
        color: String? = null,
        ownerListId: String,
        stateEvent: StateEvent
    ): Flow<DataState<NoteListViewState>?> = flow {

        val newNote = noteFactory.createNote(
            id = id,
            title = title,
            checked = false,
            color = color,
            listId = ownerListId
        )

        val cacheResult = safeCacheCall(Dispatchers.IO) {
            noteCacheDataSource.insertNote(newNote)
        }

        val cacheResponse = object : CacheResultHandler<NoteListViewState, Long>(
            result = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultValue: Long): DataState<NoteListViewState>? {
                return if (resultValue > 0) {
                    // insert success
                    val viewState = NoteListViewState(newNote = newNote)
                    DataState.data(
                        stateMessage = StateMessage(
                            message = INSERT_NOTE_SUCCESS,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Success
                        ),
                        data = viewState,
                        stateEvent = stateEvent
                    )
                } else {
                    // insert failure
                    DataState.error(
                        stateMessage = StateMessage(
                            message = INSERT_NOTE_FAILED,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Error
                        ),
                        stateEvent = stateEvent
                    )
                }
            }
        }.getResult()

        emit(cacheResponse)
        updateNetwork(cacheResponse?.stateMessage?.message, newNote)
    }

    private suspend fun updateNetwork(cacheResponse: String?, newNote: Note) {
        if (cacheResponse.equals(INSERT_NOTE_SUCCESS)) {
            safeNetworkCall(Dispatchers.IO) {
                noteNetworkDataSource.insertOrUpdateNote(newNote)
            }
        }
    }

    companion object {
        const val INSERT_NOTE_SUCCESS = "Successfully inserted new note."
        const val INSERT_NOTE_FAILED = "Failed to insert new note."
    }
}