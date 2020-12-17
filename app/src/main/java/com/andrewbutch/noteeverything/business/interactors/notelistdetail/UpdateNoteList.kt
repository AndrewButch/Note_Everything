package com.andrewbutch.noteeverything.business.interactors.notelistdetail

import com.andrewbutch.noteeverything.business.data.cache.CacheResultHandler
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteListCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteListNetworkDataSource
import com.andrewbutch.noteeverything.business.data.util.safeCacheCall
import com.andrewbutch.noteeverything.business.data.util.safeNetworkCall
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.state.*
import com.andrewbutch.noteeverything.framework.ui.notelistdetail.state.NoteListDetailViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateNoteList
@Inject
constructor(
    private val noteListCacheDataSource: NoteListCacheDataSource,
    private val noteListNetworkDataSource: NoteListNetworkDataSource
) {
    fun updateNoteList(
        noteList: NoteList,
        stateEvent: StateEvent
    ): Flow<DataState<NoteListDetailViewState>?> = flow {
        val cacheResult = safeCacheCall(Dispatchers.IO) {
            noteListCacheDataSource.updateNoteList(
                id = noteList.id,
                newTitle = noteList.title,
                newColor = noteList.color,
                timestamp = null // will be generated
            )
        }

        val cacheResponse = object : CacheResultHandler<NoteListDetailViewState, Int>(
            result = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultValue: Int): DataState<NoteListDetailViewState>? {
                return if (resultValue > 0) {
                    DataState(
                        stateMessage = StateMessage(
                            message = UPDATE_NOTE_LIST_SUCCESS,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Success
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                } else {
                    DataState(
                        stateMessage = StateMessage(
                            message = UPDATE_NOTE_LIST_FAILURE,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Error
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                }
            }

        }.getResult()

        emit(cacheResponse)

        updateNetwork(cacheResponse?.stateMessage?.message, noteList)
    }

    private suspend fun updateNetwork(responseMsg: String?, noteList: NoteList) {
        if (responseMsg == UPDATE_NOTE_LIST_SUCCESS) {
            safeNetworkCall(Dispatchers.IO) {
                noteListNetworkDataSource.insertOrUpdateNoteList(noteList)
            }
        }
    }

    companion object {
        const val UPDATE_NOTE_LIST_SUCCESS = "Successfully updated note list"
        const val UPDATE_NOTE_LIST_FAILURE = "Failed to update note list"
    }
}