package com.andrewbutch.noteeverything.business.interactors.auth

import com.andrewbutch.noteeverything.business.data.cache.CacheResultHandler
import com.andrewbutch.noteeverything.business.data.cache.abstraction.UserCacheDataSource
import com.andrewbutch.noteeverything.business.data.util.safeCacheCall
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.DataState
import com.andrewbutch.noteeverything.business.domain.state.MessageType
import com.andrewbutch.noteeverything.business.domain.state.StateMessage
import com.andrewbutch.noteeverything.business.domain.state.UIComponentType
import com.andrewbutch.noteeverything.framework.ui.auth.state.AuthStateEvent
import com.andrewbutch.noteeverything.framework.ui.auth.state.AuthViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PreviousSession
@Inject
constructor(
    private val userCacheDataSource: UserCacheDataSource
) {

    fun getPreviousSession(stateEvent: AuthStateEvent): Flow<DataState<AuthViewState>?> = flow {
        val cacheResult = safeCacheCall(IO) {
            userCacheDataSource.getPreviousUser()
        }

        val handledResult = object : CacheResultHandler<AuthViewState, User?>(
            result = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultValue: User?): DataState<AuthViewState>? {
                return if (resultValue == null) {
                    // previous session does not exist
                    DataState.error(
                        stateMessage = StateMessage(
                            message = NO_SESSION,
                            uiComponentType = UIComponentType.None,
                            messageType = MessageType.Info
                        ),
                        stateEvent = stateEvent
                    )
                }else {
                    // previous session exists
                    DataState.data(
                        stateMessage = StateMessage(
                            message = SESSION_EXISTS,
                            uiComponentType = UIComponentType.None,
                            messageType = MessageType.Info
                        ),
                        data = AuthViewState(user = resultValue),
                        stateEvent = stateEvent
                    )
                }
            }


        }.getResult()

        emit(handledResult)
    }

    companion object {
        const val SESSION_EXISTS = "Previous session exists"
        const val NO_SESSION = "Previous session doesn`t exists"
    }
}