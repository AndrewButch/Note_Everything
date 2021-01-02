package com.andrewbutch.noteeverything.framework.ui.auth

import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.*
import com.andrewbutch.noteeverything.business.interactors.auth.AuthInteractors
import com.andrewbutch.noteeverything.framework.ui.BaseViewModel
import com.andrewbutch.noteeverything.framework.ui.auth.state.AuthStateEvent
import com.andrewbutch.noteeverything.framework.ui.auth.state.AuthViewState
import com.andrewbutch.noteeverything.framework.ui.auth.state.LoginFields
import com.andrewbutch.noteeverything.framework.ui.auth.state.RegistrationFields
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    private val interactors: AuthInteractors,
    eventStore: StateEventStore,
    messageStack: MessageStack
) : BaseViewModel<AuthViewState>(eventStore, messageStack) {

    fun setStateEvent(stateEvent: AuthStateEvent) {
        val job: Flow<DataState<AuthViewState>?> = when (stateEvent) {
            is AuthStateEvent.LoginEvent -> {
                viewState.value?.loginFields?.isValidLogin().let { error ->
                    if (error == "") {
                        interactors.login.login(stateEvent.email, stateEvent.password, stateEvent)
                    } else {
                        emitStateMessageEvent(
                            stateMessage = StateMessage(
                                message = error,
                                uiComponentType = UIComponentType.Toast,
                                messageType = MessageType.Error
                            ),
                            data = AuthViewState(),
                            stateEvent = stateEvent,
                        )
                    }
                }
            }
            is AuthStateEvent.RegisterEvent -> {
                viewState.value?.registrationFields?.isValidRegistration().let { error ->
                    if (error == "") {
                        interactors.registration.register(
                            stateEvent.email,
                            stateEvent.password,
                            stateEvent
                        )
                    } else {
                        emitStateMessageEvent(
                            stateMessage = StateMessage(
                                message = error,
                                uiComponentType = UIComponentType.Dialog,
                                messageType = MessageType.Error
                            ),
                            data = AuthViewState(),
                            stateEvent = stateEvent
                        )
                    }
                }

            }
            is AuthStateEvent.CheckPreviousAuth -> {
                interactors.previousSession.getPreviousSession(stateEvent)
            }
        }
        launchJob(stateEvent, job)
    }

    override fun handleViewState(viewState: AuthViewState) {
        viewState.loginFields?.let { loginFields -> setLoginFields(loginFields) }
        viewState.registrationFields?.let { regFields -> setRegistrationFields(regFields) }
        viewState.user?.let { user -> setUser(user) }
    }

    fun setLoginFields(loginFields: LoginFields?) {
        val updated = getCurrentViewStateOrNew()
        updated.loginFields = loginFields
        setViewState(updated)
    }

    fun setRegistrationFields(registrationFields: RegistrationFields?) {
        val updated = getCurrentViewStateOrNew()
        updated.registrationFields = registrationFields
        setViewState(updated)
    }

    fun setUser(user: User?) {
        val updated = getCurrentViewStateOrNew()
        updated.user = user
        setViewState(updated)
    }

    fun getUser(): User? {
        return getCurrentViewStateOrNew().user
    }

    override fun getNewViewState(): AuthViewState = AuthViewState()
}