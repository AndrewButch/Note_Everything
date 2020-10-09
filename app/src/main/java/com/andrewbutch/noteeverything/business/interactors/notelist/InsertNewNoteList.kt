package com.andrewbutch.noteeverything.business.interactors.notelist

import com.andrewbutch.noteeverything.business.data.cache.CacheResultHandler
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteListCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteListNetworkDataSource
import com.andrewbutch.noteeverything.business.data.util.safeCacheCall
import com.andrewbutch.noteeverything.business.data.util.safeNetworkCall
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.model.NoteListFactory
import com.andrewbutch.noteeverything.business.domain.state.*
import com.andrewbutch.noteeverything.framework.ui.notelist.state.NoteListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InsertNewNoteList(
    private val noteListCacheDataSource: NoteListCacheDataSource,
    private val noteListNetworkDataSource: NoteListNetworkDataSource,
    private val noteListFactory: NoteListFactory
) {
    fun insertNewNote(
        id: String? = null,
        title: String,
        color: String? = null,
        stateEvent: StateEvent
    ): Flow<DataState<NoteListViewState>?> = flow {

        val newNoteList = noteListFactory.createNoteList(
            id = id,
            title = title,
            color = color,
        )

        val cacheResult = safeCacheCall(IO) {
            noteListCacheDataSource.insertNoteList(newNoteList)
        }

        val handledResult = object : CacheResultHandler<NoteListViewState, Long>(
            result = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultValue: Long): DataState<NoteListViewState>? {
                return if (resultValue > 0) {
                    // success
                    DataState.data(
                        stateMessage = StateMessage(
                            message = INSERT_NOTE_LIST_SUCCESS,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Success
                        ),
                        data = NoteListViewState(newNoteList = newNoteList),
                        stateEvent = stateEvent
                    )
                } else {
                    // failure
                    DataState.error(
                        stateMessage = StateMessage(
                            message = INSERT_NOTE_LIST_FAILED,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Error
                        ),
                        stateEvent = stateEvent
                    )
                }
            }

        }.getResult()
        emit(handledResult)
        updateNetwork(handledResult?.stateMessage?.message, newNoteList)
    }

    private suspend fun updateNetwork(cacheResultMsg: String?, newNoteList: NoteList) {
        if (cacheResultMsg == INSERT_NOTE_LIST_SUCCESS) {
            safeNetworkCall(IO) {
                noteListNetworkDataSource.insertOrUpdateNoteList(noteList = newNoteList)
            }
        }
    }

    companion object {
        const val INSERT_NOTE_LIST_SUCCESS = "Successfully inserted new note list."
        const val INSERT_NOTE_LIST_FAILED = "Failed to insert new note list."
    }
}