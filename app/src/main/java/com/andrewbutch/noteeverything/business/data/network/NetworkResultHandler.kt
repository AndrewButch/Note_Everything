package com.andrewbutch.noteeverything.business.data.network

import com.andrewbutch.noteeverything.business.data.network.NetworkConstants.NETWORK_ERROR
import com.andrewbutch.noteeverything.business.domain.state.*

abstract class NetworkResultHandler<ViewState, Data>(
    private val result: NetworkResult<Data>,
    private val stateEvent: StateEvent,
) {

    suspend fun getResult(): DataState<ViewState>? {
        return when (result) {
            is NetworkResult.Error -> {
                DataState.error(
                    stateMessage = StateMessage(
                        message = "Event: ${stateEvent.eventName()}, " +
                                "Info: ${stateEvent.errorInfo()}, " +
                                "Reason: ${result.errorMessage}",
                        uiComponentType = UIComponentType.Dialog,
                        messageType = MessageType.Error
                    ),
                    stateEvent = stateEvent
                )
            }

            is NetworkResult.NetworkError -> {
                DataState.error(
                    stateMessage = StateMessage(
                        message = "Event: ${stateEvent.eventName()}, " +
                                "Info: ${stateEvent.errorInfo()}, " +
                                "Reason: $NETWORK_ERROR",
                        uiComponentType = UIComponentType.Dialog,
                        messageType = MessageType.Error
                    ),
                    stateEvent = stateEvent
                )
            }

            is NetworkResult.Success -> {
                if (result.value == null) {
                    return DataState.error(
                        stateMessage = StateMessage(
                            message = "Event: ${stateEvent.eventName()}, " +
                                    "Info: ${stateEvent.errorInfo()}, " +
                                    "Reason: $NETWORK_ERROR",
                            uiComponentType = UIComponentType.Dialog,
                            messageType = MessageType.Error
                        ),
                        stateEvent = stateEvent
                    )
                }
                handleSuccess(result.value)
            }
        }
    }

    abstract suspend fun handleSuccess(resultValue: Data): DataState<ViewState>
}