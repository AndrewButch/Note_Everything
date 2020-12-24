package com.andrewbutch.noteeverything.business.domain.state

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class JobLauncher<ViewState>
constructor(val eventStore: StateEventStore, val messageStack: MessageStack) {

    private var coroutineScope: CoroutineScope? = null

    fun launchJob(
        stateEvent: StateEvent,
        job: Flow<DataState<ViewState>?>
    ) {
        if (canExecuteStateEvent(stateEvent)) {
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
                .launchIn(getCoroutineScope())
        }
    }

    private fun canExecuteStateEvent(stateEvent: StateEvent): Boolean {
        // Check StateEvent is active for prevent duplication
        if (isStateEventActive(stateEvent)) {
            return false
        }
        // if has messages, means message not handled (maybe dialog window)
//        if (!isMessageStackEmpty()) {
//            return false
//        }
        return true
    }

    private fun isMessageStackEmpty(): Boolean {
        return messageStack.isMessageStackEmpty()
    }

    private fun isStateEventActive(stateEvent: StateEvent): Boolean {
        return eventStore.isStateEventActive(stateEvent)
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

    private fun getCoroutineScope(): CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun cancelActiveJob() {
        coroutineScope?.let { scope ->
            if (scope.isActive) {
                scope.cancel()
            }
            coroutineScope = null
        }

        messageStack.removeAllMessages()
    }
}