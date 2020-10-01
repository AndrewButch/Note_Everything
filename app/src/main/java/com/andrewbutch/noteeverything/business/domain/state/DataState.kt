package com.andrewbutch.noteeverything.business.domain.state

class DataState<T>(
    val stateMessage: StateMessage? = null,
    val data: T? = null,
    val stateEvent: StateEvent? = null,
) {

    companion object {
        fun <T> error(stateMessage: StateMessage, stateEvent: StateEvent?): DataState<T> =
            DataState(stateMessage = stateMessage, stateEvent = stateEvent)

        fun <T> data(
            stateMessage: StateMessage,
            data: T?,
            stateEvent: StateEvent?
        ): DataState<T> =
            DataState(stateMessage = stateMessage, data = data, stateEvent = stateEvent)
    }
}