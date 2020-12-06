package com.andrewbutch.noteeverything.business.domain.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber
import javax.inject.Singleton

@Singleton
class StateEventStore {
    private val events = HashMap<String, StateEvent>()

    private val _shouldDisplayProgressBar = MutableLiveData<Boolean>()
    val shouldDisplayProgressBar: LiveData<Boolean>
        get() = _shouldDisplayProgressBar

    fun addEvent(event: StateEvent) {
        Timber.d("Add event: ${event.eventName()}")
        events[event.eventName()] = event
        syncEvents()
    }

    fun removeEvent(event: StateEvent) {
        Timber.d("Remove event: ${event.eventName()}")
        events.remove(event.eventName())
        syncEvents()
    }

    private fun syncEvents() {
        var displayProgressBar = false
        for (event in events.values) {
            if (event.shouldDisplayProgressBar()) {
                displayProgressBar = true
            }
        }
        _shouldDisplayProgressBar.value = displayProgressBar
    }
}