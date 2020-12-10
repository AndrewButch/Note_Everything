package com.andrewbutch.noteeverything.framework.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrewbutch.noteeverything.business.domain.state.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class BaseViewModel<ViewState>
constructor(eventStore: StateEventStore, messageStack: MessageStack) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = _viewState

    private val jobLauncher = object : JobLauncher<ViewState>(eventStore, messageStack) {
        override fun handleViewState(viewState: ViewState) {
            this@BaseViewModel.handleViewState(viewState)
        }

    }

    fun setViewState(viewState: ViewState) {
        _viewState.value = viewState
    }

    abstract fun handleViewState(viewState: ViewState)

    protected fun getCurrentViewStateOrNew() = _viewState.value ?: getNewViewState()

    abstract fun getNewViewState(): ViewState

    protected open fun emitStateMessageEvent(
        stateMessage: StateMessage,
        data: ViewState,
        stateEvent: StateEvent
    ) = flow {
        emit(
            DataState.data(
                stateMessage = stateMessage,
                data = data,
                stateEvent = stateEvent
            )
        )
    }

    fun launchJob(stateEvent: StateEvent, job: Flow<DataState<ViewState>?>) {
        jobLauncher.launchJob(stateEvent, job)
    }

    fun getStateMessage(): LiveData<StateMessage?> = jobLauncher.messageStack.stateMessage


    fun removeStateMessage() = jobLauncher.messageStack.removeMessage()


    fun shouldDisplayProgressBar(): LiveData<Boolean> =
        jobLauncher.eventStore.shouldDisplayProgressBar


}