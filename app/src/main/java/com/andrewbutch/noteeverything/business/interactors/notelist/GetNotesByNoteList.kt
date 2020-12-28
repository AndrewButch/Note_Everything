package com.andrewbutch.noteeverything.business.interactors.notelist

import com.andrewbutch.noteeverything.business.data.cache.CacheResultHandler
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteCacheDataSource
import com.andrewbutch.noteeverything.business.data.util.safeCacheCall
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.state.*
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetNotesByNoteList
constructor(
    private val noteCacheDataSource: NoteCacheDataSource

) {
    fun getNotesByNoteList(
        stateEvent: StateEvent,
        ownerListId: String,
        filterAndOrder: String?
    ): Flow<DataState<NoteListViewState>?> = flow {
        val cacheResult = safeCacheCall(Dispatchers.IO) {
            noteCacheDataSource.getNotesByOwnerListId(ownerListId, filterAndOrder)
        }

        val cacheResponse = object : CacheResultHandler<NoteListViewState, List<Note>>(
            result = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultValue: List<Note>): DataState<NoteListViewState>? {
                val uiComponentType: UIComponentType = UIComponentType.None
                val messageType: MessageType = MessageType.Success
                val message = if (resultValue.isEmpty()) {
                    GET_NOTES_EMPTY
                } else {
                    GET_NOTES_SUCCESS
                }

                return DataState.data(
                    stateMessage = StateMessage(
                        message = message,
                        uiComponentType = uiComponentType,
                        messageType = messageType
                    ),
                    data = NoteListViewState(notes = ArrayList(resultValue)),
                    stateEvent = stateEvent
                )
            }
        }.getResult()
        emit(cacheResponse)
    }

    companion object {
        const val GET_NOTES_SUCCESS = "Successfully retrieved all notes by list id"
        const val GET_NOTES_EMPTY = "List has no notes"
    }
}