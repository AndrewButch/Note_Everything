package com.andrewbutch.noteeverything.framework.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrewbutch.noteeverything.business.domain.state.DataState
import com.andrewbutch.noteeverything.business.domain.state.StateEvent
import com.andrewbutch.noteeverything.business.domain.state.StateMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class BaseViewModel<ViewState> : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = _viewState

    protected fun setViewState(viewState: ViewState) {
        _viewState.value = viewState
    }

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

    abstract fun launchJob(job: Flow<DataState<ViewState>?>)


}