package com.andrewbutch.noteeverything.business.data.cache

import com.andrewbutch.noteeverything.business.data.cache.CacheConstants.CACHE_DATA_NULL
import com.andrewbutch.noteeverything.business.domain.state.*

abstract class CacheResultHandler<ViewState, Data>(
    private val result: CacheResult<Data?>,
    private val stateEvent: StateEvent?
) {

    suspend fun getResult(): DataState<ViewState>? {
        return when (result) {
            is CacheResult.Error -> {
                DataState.error(
                    stateMessage = StateMessage(
                        message = "Event: ${stateEvent?.eventName()}, " +
                                "Info: ${stateEvent?.errorInfo()}, " +
                                "Reason: ${result.errorMessage}",
                        uiComponentType = UIComponentType.Dialog,
                        messageType = MessageType.Error
                    ),
                    stateEvent = stateEvent
                )
            }

            is CacheResult.Success -> {
                if (result.value == null) {
                    return DataState.error(
                        stateMessage = StateMessage(
                            message = "Event: ${stateEvent?.eventName()}, " +
                                    "Info: ${stateEvent?.errorInfo()}, " +
                                    "Reason: $CACHE_DATA_NULL",
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

    abstract suspend fun handleSuccess(resultValue: Data): DataState<ViewState>?

}