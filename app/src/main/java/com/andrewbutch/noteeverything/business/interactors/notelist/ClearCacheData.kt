package com.andrewbutch.noteeverything.business.interactors.notelist

import com.andrewbutch.noteeverything.business.data.cache.CacheResultHandler
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteListCacheDataSource
import com.andrewbutch.noteeverything.business.data.util.safeCacheCall
import com.andrewbutch.noteeverything.business.domain.state.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ClearCacheData<ViewState>
constructor(
    private val noteListCacheDataSource: NoteListCacheDataSource,
) {
    fun clear(
        stateEvent: StateEvent
    ): Flow<DataState<ViewState>?> = flow {
        val cacheResult = safeCacheCall(IO) {
            noteListCacheDataSource.deleteAllNoteLists()
        }

        val handleResult = object : CacheResultHandler<ViewState, Int>(
            result = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultValue: Int): DataState<ViewState>? {
                return if (resultValue > 0) {
                    DataState.data(
                        stateMessage = StateMessage(
                            message = DELETE_ALL_LISTS_SUCCESS,
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
                            message = DELETE_ALL_LISTS_FAILED,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Error
                        ),
                        stateEvent = stateEvent
                    )
                }
            }
        }.getResult()
        emit(handleResult)
    }

    companion object {
        const val DELETE_ALL_LISTS_SUCCESS = "Successfully deleted all note lists."
        const val DELETE_ALL_LISTS_FAILED = "Failed to delete all note lists."
    }
}