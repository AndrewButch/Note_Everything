package com.andrewbutch.noteeverything.business.domain.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

abstract class JobLauncher<ViewState> {

    @Inject
    lateinit var eventStore: StateEventStore

    @Inject
    lateinit var messageStack: MessageStack

    init {
        Timber.d("Init job launcher")
    }
    fun launchJob(
        stateEvent: StateEvent,
        job: Flow<DataState<ViewState>?>
    ) {
        eventStore.addEvent(stateEvent)
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

    fun handleStateMessage(stateMessage: StateMessage) {
        messageStack.addMessage(stateMessage)
    }

    private fun removeStateEvent(stateEvent: StateEvent) {
        eventStore.removeEvent(stateEvent)
    }

    fun addStateEvent(stateEvent: StateEvent) {
        // Добавть событие в хранилище
    }
}