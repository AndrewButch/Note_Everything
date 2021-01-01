package com.andrewbutch.noteeverything.framework.ui.sync

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.interactors.splash.SyncNoteLists
import com.andrewbutch.noteeverything.business.interactors.splash.SyncNotes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SyncManager
@Inject
constructor(
    private val syncNoteLists: SyncNoteLists,
    private val syncNotes: SyncNotes,
) {
    private var _syncCompleted: MutableLiveData<Boolean> = MutableLiveData(false)

    val syncCompleted: LiveData<Boolean>
        get() = _syncCompleted

    fun sync(user: User, coroutineScope: CoroutineScope) {
        if (_syncCompleted.value!!) {
            return
        }

        val syncJob = coroutineScope.launch {
            val noteListsJob = launch {
                syncNoteLists.syncNoteLists(user)
            }

            noteListsJob.join()
            val allNoteLists = syncNoteLists.noteListCacheDataSource.getAllNoteLists()

            launch {
                syncNotes.syncNotes(allNoteLists, user)
            }
        }

        syncJob.invokeOnCompletion {
            CoroutineScope(Dispatchers.Main).launch {
                _syncCompleted.value = true
            }
        }
    }
}