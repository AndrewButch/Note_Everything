package com.andrewbutch.noteeverything.business.interactors.common

import com.andrewbutch.noteeverything.business.data.cache.CacheResultHandler
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.data.util.safeCacheCall
import com.andrewbutch.noteeverything.business.data.util.safeNetworkCall
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.state.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteNote<ViewState>
@Inject
constructor(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource,
) {

    fun deleteNote(
        note: Note,
        stateEvent: StateEvent
    ): Flow<DataState<ViewState>?> = flow {
        val cacheResult = safeCacheCall(IO) {
            noteCacheDataSource.deleteNote(note.id)
        }

        val handledResult = object : CacheResultHandler<ViewState, Int>(
            result = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultValue: Int): DataState<ViewState>? {
                return if (resultValue > 0) {
                    // success
                    DataState.data(
                        stateMessage = StateMessage(
                            message = DELETE_NOTE_SUCCESS,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Success
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                } else {
                    // failure
                    DataState.error(
                        stateMessage = StateMessage(
                            message = DELETE_NOTE_FAILED,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Error
                        ),
                        stateEvent = stateEvent
                    )
                }
            }

        }.getResult()

        emit(handledResult)

        updateNetwork(handledResult?.stateMessage?.message, note)
    }

    private suspend fun updateNetwork(message: String?, note: Note) {
        if (message == DELETE_NOTE_SUCCESS) {
            safeNetworkCall(IO) {
                noteNetworkDataSource.deleteNote(note)
            }
            // TODO(insert to deleted node)
        }
    }


    companion object {
        const val DELETE_NOTE_SUCCESS = "Successfully deleted note."
        const val DELETE_NOTE_FAILED = "Failed to delete note."
    }
}