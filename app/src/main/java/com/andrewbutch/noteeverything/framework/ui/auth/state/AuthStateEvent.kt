package com.andrewbutch.noteeverything.framework.ui.auth.state

import com.andrewbutch.noteeverything.business.domain.state.StateEvent

sealed class AuthStateEvent : StateEvent {

    data class LoginEvent(
        val email: String,
        val password: String
    ): AuthStateEvent() {
        override fun errorInfo(): String {
            return "Error while login"
        }

        override fun eventName(): String {
            return "LoginEvent"

        }

        override fun shouldDisplayProgressBar() = true

    }

    data class RegisterEvent(
        val email: String,
        val password: String,
        val confirmPassword: String
    ): AuthStateEvent() {
        override fun errorInfo(): String {
            return "Error registration"
        }

        override fun eventName(): String {
            return "RegisterEvent"
        }

        override fun shouldDisplayProgressBar() = true

    }

    object CheckPreviousAuth : AuthStateEvent() {
        override fun errorInfo(): String {
            return "Error getting previous auth user"
        }

        override fun eventName(): String {
            return "CheckPreviousAuth"
        }

        override fun shouldDisplayProgressBar() = true

    }


}