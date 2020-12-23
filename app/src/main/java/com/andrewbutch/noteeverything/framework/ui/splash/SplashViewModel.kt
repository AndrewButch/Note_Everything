package com.andrewbutch.noteeverything.framework.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrewbutch.noteeverything.business.domain.model.User
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val syncManager: SyncManager) : ViewModel() {


    fun syncHasBeenExecuted() = syncManager.syncCompleted

    fun syncCacheWithNetwork(user: User) {
        syncManager.sync(user, viewModelScope)
    }
}