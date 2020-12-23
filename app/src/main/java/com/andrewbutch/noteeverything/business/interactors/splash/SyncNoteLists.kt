package com.andrewbutch.noteeverything.business.interactors.splash

import com.andrewbutch.noteeverything.business.data.cache.CacheResultHandler
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteListCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.NetworkResultHandler
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteListNetworkDataSource
import com.andrewbutch.noteeverything.business.data.util.safeCacheCall
import com.andrewbutch.noteeverything.business.data.util.safeNetworkCall
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

class SyncNoteLists
@Inject
constructor(
    val noteListCacheDataSource: NoteListCacheDataSource,
    private val noteListNetworkDataSource: NoteListNetworkDataSource
) {
    suspend fun syncNoteLists(user: User) {
        val cachedNoteLists = getCachedNoteLists()

        val networkNoteLists = getNetworkNoteLists(user)

        syncNetworkNoteListsWithCached(
            ArrayList(cachedNoteLists),
            networkNoteLists,
            user
        )
    }


    private suspend fun getCachedNoteLists(): List<NoteList> {
        val cacheResult = safeCacheCall(Dispatchers.IO) {
            noteListCacheDataSource.getAllNoteLists()
        }

        val response = object : CacheResultHandler<List<NoteList>, List<NoteList>>(
            result = cacheResult,
            stateEvent = null
        ) {
            override suspend fun handleSuccess(resultValue: List<NoteList>): DataState<List<NoteList>>? {
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

    private suspend fun getNetworkNoteLists(user: User): List<NoteList> {
        val networkResult = safeNetworkCall(Dispatchers.IO) {
            noteListNetworkDataSource.getAllNoteLists(user)
        }

        val response = object : NetworkResultHandler<List<NoteList>, List<NoteList>>(
            result = networkResult,
            stateEvent = null
        ) {
            override suspend fun handleSuccess(resultValue: List<NoteList>): DataState<List<NoteList>> {
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

    private suspend fun syncNetworkNoteListsWithCached(
        cachedNoteLists: ArrayList<NoteList>,
        networkNoteLists: List<NoteList>,
        user: User
    ) = withContext(Dispatchers.IO) {
        for (networkNoteList in networkNoteLists) {
            noteListCacheDataSource.searchNoteListById(networkNoteList.id)?.let { cachedNoteList ->
                cachedNoteLists.remove(cachedNoteList)
                updateCacheOrNetwork(cachedNoteList, networkNoteList, user)
            } ?: noteListCacheDataSource.insertNoteList(networkNoteList)
        }

        // insert remaining into network
        for (cachedNoteList in cachedNoteLists) {
            noteListNetworkDataSource.insertOrUpdateNoteList(cachedNoteList, user)
        }
    }

    private suspend fun updateCacheOrNetwork(
        cachedNoteList: NoteList,
        networkNoteList: NoteList,
        user: User
    ) {
        val cachedUpdatedAt = cachedNoteList.updatedAt
        val networkUpdatedAt = networkNoteList.updatedAt

        // update cache (network has newest data)
        if (networkUpdatedAt > cachedUpdatedAt) {
            Timber.d(
                "cachedUpdatedAt: $cachedUpdatedAt, " +
                        "networkUpdatedAt: $networkUpdatedAt, " +
                        "noteList: ${cachedNoteList.title}"
            )
            safeCacheCall(Dispatchers.IO) {
                noteListCacheDataSource.updateNoteList(
                    id = networkNoteList.id,
                    newTitle = networkNoteList.title,
                    newColor = networkNoteList.color,
                    timestamp = networkNoteList.updatedAt
                )
            }
        }
        // update network (cache has newest data)
        else if (networkUpdatedAt < cachedUpdatedAt) {
            Timber.d(
                "networkUpdatedAt: $networkUpdatedAt, " +
                        "cachedUpdatedAt: $cachedUpdatedAt, " +
                        "noteList: ${cachedNoteList.title}"
            )
            safeNetworkCall(Dispatchers.IO) {
                noteListNetworkDataSource.insertOrUpdateNoteList(cachedNoteList, user)
            }
        }
    }
}