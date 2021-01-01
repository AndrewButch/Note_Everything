package com.andrewbutch.noteeverything.framework.ui.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrewbutch.noteeverything.business.domain.model.User
import javax.inject.Inject

// View model without ViewState. All variables in LiveData
class SyncViewModel @Inject constructor(private val syncManager: SyncManager) : ViewModel() {


    fun syncHasBeenExecuted() = syncManager.syncCompleted

    fun syncCacheWithNetwork(user: User) {
        syncManager.sync(user, viewModelScope)
    }
}