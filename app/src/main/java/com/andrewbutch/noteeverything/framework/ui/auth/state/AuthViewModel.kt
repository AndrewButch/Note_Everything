package com.andrewbutch.noteeverything.framework.ui.auth.state

import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.business.domain.state.MessageStack
import com.andrewbutch.noteeverything.business.domain.state.StateEventStore
import com.andrewbutch.noteeverything.framework.ui.BaseViewModel
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    eventStore: StateEventStore,
    messageStack: MessageStack
) : BaseViewModel<AuthViewState>(eventStore, messageStack) {

    fun setStateEvent(stateEvent: AuthStateEvent) {
        when (stateEvent) {
            is AuthStateEvent.LoginEvent -> {

            }
            is AuthStateEvent.RegisterEvent -> {

            }
            is AuthStateEvent.CheckPreviousAuth -> {

            }
        }
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

    override fun getNewViewState(): AuthViewState = AuthViewState()
}