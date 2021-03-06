package com.andrewbutch.noteeverything.business.interactors.common

import com.andrewbutch.noteeverything.business.data.cache.CacheResultHandler
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteListCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteListNetworkDataSource
import com.andrewbutch.noteeverything.business.data.util.safeCacheCall
import com.andrewbutch.noteeverything.business.data.util.safeNetworkCall
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteNoteList<ViewState>
@Inject
constructor(
    private val noteListCacheDataSource: NoteListCacheDataSource,
    private val noteListNetworkDataSource: NoteListNetworkDataSource,
) {

    fun deleteNoteList(
        noteList: NoteList,
        user: User,
        stateEvent: StateEvent
    ): Flow<DataState<ViewState>?> = flow {
        val cacheResult = safeCacheCall(IO) {
            noteListCacheDataSource.deleteNoteList(noteList.id)
        }

        val handledResult = object : CacheResultHandler<ViewState, Int>(
            result = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultValue: Int): DataState<ViewState>? {
                return if (resultValue > 0) {
                    // success delete
                    DataState.data(
                        stateMessage = StateMessage(
                            message = DELETE_NOTE_LIST_SUCCESS,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Success
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                } else {
                    // failure delete
                    DataState.error(
                        stateMessage = StateMessage(
                            message = DELETE_NOTE_LIST_FAILED,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Error
                        ),
                        stateEvent = stateEvent
                    )
                }
            }

        }.getResult()

        emit(handledResult)
        updateNetwork(handledResult?.stateMessage?.message, noteList, user)
    }

    private suspend fun updateNetwork(message: String?, noteList: NoteList, user: User) {
        if (DELETE_NOTE_LIST_SUCCESS == message) {
            safeNetworkCall(IO) {
                noteListNetworkDataSource.deleteNoteList(noteList.id, user)
            }
            // TODO(insert to deleted node)
        }
    }

    companion object {
        const val DELETE_NOTE_LIST_SUCCESS = "Successfully deleted note list."
        const val DELETE_NOTE_LIST_FAILED = "Failed to delete note list."
    }
}