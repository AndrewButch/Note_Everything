package com.andrewbutch.noteeverything.business.interactors.splash

import com.andrewbutch.noteeverything.business.data.cache.CacheResultHandler
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.NetworkResultHandler
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.data.util.safeCacheCall
import com.andrewbutch.noteeverything.business.data.util.safeNetworkCall
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.DataState
import com.andrewbutch.noteeverything.business.domain.state.MessageType
import com.andrewbutch.noteeverything.business.domain.state.StateMessage
import com.andrewbutch.noteeverything.business.domain.state.UIComponentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SyncNotes
@Inject
constructor(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource,
) {
    suspend fun syncNotes(noteLists: List<NoteList>, user: User) {
        val cachedNotes = ArrayList<Note>()
        val networkNotes = ArrayList<Note>()
        for (noteList in noteLists) {
            cachedNotes.addAll(getCachedNotes(noteList))
            networkNotes.addAll(getNetworkNotes(noteList, user))
        }

        syncNetworkNotesWithCached(
            cachedNotes,
            networkNotes,
            user
        )
    }


    private suspend fun getCachedNotes(noteList: NoteList): List<Note> {
        val cacheResult = safeCacheCall(Dispatchers.IO) {
            noteCacheDataSource.getNotesByOwnerListId(noteList.id)
        }

        val response = object : CacheResultHandler<List<Note>, List<Note>>(
            result = cacheResult,
            stateEvent = null
        ) {
            override suspend fun handleSuccess(resultValue: List<Note>): DataState<List<Note>>? {
                return DataState.data(
                    stateMessage = StateMessage(
                        message = null,
                        uiComponentType = UIComponentType.None,
                        messageType = MessageType.None
                    ),
                    data = resultValue,
                    stateEvent = null
                )
            }

        }.getResult()

        return response?.data ?: emptyList()
    }

    private suspend fun getNetworkNotes(noteList: NoteList, user: User): List<Note> {
        val networkResult = safeNetworkCall(Dispatchers.IO) {
            noteNetworkDataSource.getNotesByOwnerListId(noteList.id, user)
        }

        val response = object : NetworkResultHandler<List<Note>, List<Note>>(
            result = networkResult,
            stateEvent = null
        ) {
            override suspend fun handleSuccess(resultValue: List<Note>): DataState<List<Note>> {
                return DataState.data(
                    stateMessage = StateMessage(
                        message = null,
                        uiComponentType = UIComponentType.None,
                        messageType = MessageType.None
                    ),
                    data = resultValue,
                    stateEvent = null
                )
            }

        }.getResult()

        return response?.data ?: emptyList()
    }

    private suspend fun syncNetworkNotesWithCached(
        cachedNotes: ArrayList<Note>,
        networkNotes: List<Note>,
        user: User
    ) = withContext(Dispatchers.IO) {
        for (networkNote in networkNotes) {
            noteCacheDataSource.searchNoteById(networkNote.id)?.let { cachedNote ->
                cachedNotes.remove(cachedNote)
                updateCacheOrNetwork(cachedNote, networkNote, user)
            } ?: noteCacheDataSource.insertNote(networkNote)
        }

        // insert remaining into network
        for (cachedNote in cachedNotes) {
            noteNetworkDataSource.insertOrUpdateNote(cachedNote, user)
        }
    }

    private suspend fun updateCacheOrNetwork(
        cachedNote: Note,
        networkNote: Note,
        user: User
    ) {
        val cachedUpdatedAt = cachedNote.updatedAt
        val networkUpdatedAt = networkNote.updatedAt

        // update cache (network has newest data)
        if (networkUpdatedAt > cachedUpdatedAt) {
            Timber.d(
                "cachedUpdatedAt: $cachedUpdatedAt, " +
                        "networkUpdatedAt: $networkUpdatedAt, " +
                        "noteList: ${cachedNote.title}"
            )
            safeCacheCall(Dispatchers.IO) {
                noteCacheDataSource.updateNote(
                    id = networkNote.id,
                    newTitle = networkNote.title,
                    newColor = networkNote.color,
                    timestamp = networkNote.updatedAt,
                    completed = networkNote.completed
                )
            }
        }
        // update network (cache has newest data)
        else if (networkUpdatedAt < cachedUpdatedAt) {
            Timber.d(
                "networkUpdatedAt: $networkUpdatedAt, " +
                        "cachedUpdatedAt: $cachedUpdatedAt, " +
                        "noteList: ${cachedNote.title}"
            )
            safeNetworkCall(Dispatchers.IO) {
                noteNetworkDataSource.insertOrUpdateNote(cachedNote, user)
            }
        }
    }
}