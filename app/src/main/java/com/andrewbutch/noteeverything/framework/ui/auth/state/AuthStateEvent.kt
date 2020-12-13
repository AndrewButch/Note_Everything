package com.andrewbutch.noteeverything.framework.ui.auth.state

sealed class AuthStateEvent {

    data class LoginEvent(
        val email: String,
        val password: String
    ): AuthStateEvent()

    data class RegisterEvent(
        val email: String,
        val password: String,
        val confirmPassword: String
    ): AuthStateEvent()

    object CheckPreviousAuth : AuthStateEvent()


}