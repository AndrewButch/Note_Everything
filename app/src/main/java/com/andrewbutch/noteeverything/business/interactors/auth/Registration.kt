package com.andrewbutch.noteeverything.business.interactors.auth

import com.andrewbutch.noteeverything.business.data.network.NetworkResultHandler
import com.andrewbutch.noteeverything.business.data.network.abstraction.UserNetworkDataSource
import com.andrewbutch.noteeverything.business.data.util.safeNetworkCall
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.DataState
import com.andrewbutch.noteeverything.business.domain.state.MessageType
import com.andrewbutch.noteeverything.business.domain.state.StateMessage
import com.andrewbutch.noteeverything.business.domain.state.UIComponentType
import com.andrewbutch.noteeverything.framework.ui.auth.state.AuthStateEvent
import com.andrewbutch.noteeverything.framework.ui.auth.state.AuthViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class Registration
@Inject
constructor(
    private val userNetworkDataSource: UserNetworkDataSource
) {
    fun register(
        email: String,
        password: String,
        stateEvent: AuthStateEvent
    ): Flow<DataState<AuthViewState>?> = flow {

        val networkResult = safeNetworkCall(Dispatchers.IO) {
            userNetworkDataSource.registration(email, password)
        }

        val handledResult = object : NetworkResultHandler<AuthViewState, User?>(
            result = networkResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultValue: User?): DataState<AuthViewState>? {
                return if (resultValue != null) {
                    DataState.data(
                        stateMessage = StateMessage(
                            message = REGISTRATION_SUCCESS,
                            uiComponentType = UIComponentType.None,
                            messageType = MessageType.Success
                        ),
                        data = AuthViewState(user = resultValue),
                        stateEvent = stateEvent
                    )
                } else {
                    DataState.error(
                        stateMessage = StateMessage(
                            message = REGISTRATION_ERROR,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Error
                        ),
                        stateEvent = stateEvent
                    )
                }
            }

        }.getResult()
        emit(handledResult)
    }

    companion object {
        const val REGISTRATION_SUCCESS = "Successfully logged in"
        const val REGISTRATION_ERROR = "Login failure"
    }
}