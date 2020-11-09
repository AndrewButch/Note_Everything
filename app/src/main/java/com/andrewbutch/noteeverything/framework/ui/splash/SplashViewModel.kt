package com.andrewbutch.noteeverything.framework.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val syncManager: SyncManager) : ViewModel() {

    init {
        syncCacheWithNetwork()
    }

    fun syncHasBeenExecuted() = syncManager.syncCompleted

    private fun syncCacheWithNetwork() {
        syncManager.sync(viewModelScope)
    }
}