package com.andrewbutch.noteeverything.business.interactors.notedetail

import com.andrewbutch.noteeverything.business.data.cache.CacheResultHandler
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.data.util.safeCacheCall
import com.andrewbutch.noteeverything.business.data.util.safeNetworkCall
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateNote<ViewState>
@Inject
constructor(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
) {

    fun updateNote(
        note: Note,
        user: User,
        stateEvent: StateEvent
    ): Flow<DataState<ViewState>?> = flow {
        val cacheResult = safeCacheCall(Dispatchers.IO) {
            noteCacheDataSource.updateNote(
                id = note.id,
                newTitle = note.title,
                newColor = note.color,
                timestamp = null, // will be generated
                completed = note.completed
            )
        }

        val cacheResponse = object : CacheResultHandler<ViewState, Int>(
            result = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultValue: Int): DataState<ViewState>? {
                return if (resultValue > 0) {
                    DataState(
                        stateMessage = StateMessage(
                            message = UPDATE_NOTE_SUCCESS,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Success
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                } else {
                    DataState(
                        stateMessage = StateMessage(
                            message = UPDATE_NOTE_FAILURE,
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

        updateNetwork(cacheResponse?.stateMessage?.message, note, user)
    }

    private suspend fun updateNetwork(responseMsg: String?, note: Note, user: User) {
        if (responseMsg == UPDATE_NOTE_SUCCESS) {
            safeNetworkCall(Dispatchers.IO) {
                noteNetworkDataSource.insertOrUpdateNote(note, user)
            }
        }
    }

    companion object {
        const val UPDATE_NOTE_SUCCESS = "Successfully updated note"
        const val UPDATE_NOTE_FAILURE = "Failed to update note"
        const val UPDATE_NOTE_EXCEPTION = "Exception while update note"
    }
}