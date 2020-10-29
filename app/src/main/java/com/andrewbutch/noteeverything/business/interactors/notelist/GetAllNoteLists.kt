package com.andrewbutch.noteeverything.business.interactors.notelist

import com.andrewbutch.noteeverything.business.data.cache.CacheResultHandler
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteListCacheDataSource
import com.andrewbutch.noteeverything.business.data.util.safeCacheCall
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.state.*
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAllNoteLists(
    private val noteListCacheDataSource: NoteListCacheDataSource
) {
    fun getAllNoteLists(stateEvent: StateEvent): Flow<DataState<NoteListViewState>?> = flow {

        val cacheResult = safeCacheCall(Dispatchers.IO) {
            noteListCacheDataSource.getAllNoteLists()
        }

        val cacheResponse = object :
            CacheResultHandler<NoteListViewState, List<NoteList>>(
                result = cacheResult,
                stateEvent = stateEvent
            ) {
            override suspend fun handleSuccess(resultValue: List<NoteList>): DataState<NoteListViewState>? {
                val uiComponentType: UIComponentType = UIComponentType.None
                val messageType: MessageType = MessageType.Success
                val message = if (resultValue.isEmpty()) {
                    GET_ALL_NOTE_LIST_EMPTY
                } else {
                    GET_ALL_NOTE_LIST_SUCCESS
                }

                return DataState.data(
                    stateMessage = StateMessage(
                        message = message,
                        uiComponentType = uiComponentType,
                        messageType = messageType
                    ),
                    data = NoteListViewState(noteLists = resultValue),
                    stateEvent = stateEvent
                )
            }

        }.getResult()

        emit(cacheResponse)

    }

    companion object {
        const val GET_ALL_NOTE_LIST_SUCCESS = "Successfully retrieved all note lists"
        const val GET_ALL_NOTE_LIST_EMPTY = "No lists"
    }
}