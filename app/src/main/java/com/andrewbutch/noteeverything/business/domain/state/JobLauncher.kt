package com.andrewbutch.noteeverything.business.domain.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

abstract class JobLauncher<ViewState>
constructor(val eventStore: StateEventStore, val messageStack: MessageStack) {

    fun launchJob(
        stateEvent: StateEvent,
        job: Flow<DataState<ViewState>?>
    ) {
        addStateEvent(stateEvent)
        job
            .onEach {
                withContext(Dispatchers.Main) {
                    it?.data?.let { viewState ->
                        handleViewState(viewState)
                    }
                    it?.stateMessage?.let { stateMessage ->
                        handleStateMessage(stateMessage)
                    }
                    it?.stateEvent?.let { stateEvent ->
                        removeStateEvent(stateEvent)
                    }
                }
            }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    abstract fun handleViewState(viewState: ViewState)

    private fun handleStateMessage(stateMessage: StateMessage) {
        messageStack.addMessage(stateMessage)
    }

    private fun removeStateEvent(stateEvent: StateEvent) {
        eventStore.removeEvent(stateEvent)
    }

    private fun addStateEvent(stateEvent: StateEvent) {
        eventStore.addEvent(stateEvent)
    }
}