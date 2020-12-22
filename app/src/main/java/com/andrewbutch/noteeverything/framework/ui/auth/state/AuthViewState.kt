package com.andrewbutch.noteeverything.framework.ui.auth.state

import com.andrewbutch.noteeverything.business.domain.model.User


data class AuthViewState(
    var loginFields: LoginFields? = LoginFields(),
    var registrationFields: RegistrationFields? = RegistrationFields(),
    var user: User? = null
)

data class LoginFields(
    val email: String? = null,
    val password: String? = null
) {

    fun isValidLogin(): String {
        val builder = StringBuilder()
        if (email.isNullOrEmpty()) {
            builder.append(EMAIL_EMPTY_ERROR)
        }
        if (password.isNullOrEmpty()) {
            builder.append(PASSWORD_EMPTY_ERROR)
        }
        return builder.toString()
    }

    companion object {
        const val EMAIL_EMPTY_ERROR = "Fill email field"
        const val PASSWORD_EMPTY_ERROR = "Fill password field"
    }
}

data class RegistrationFields(
    val email: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null
) {

    fun isValidRegistration(): String {
        val builder = StringBuilder()

        if (email.isNullOrEmpty()) {
            builder.append(EMAIL_EMPTY_ERROR)
        }
        if (password != confirmPassword) {
            builder.append(PASSWORD_MATCH_ERROR)
        }
        if (password.isNullOrEmpty()) {
            builder.append(PASSWORD_EMPTY_ERROR)
        }
        if (confirmPassword.isNullOrEmpty()) {
            builder.append(CONFIRM_PASSWORD_EMPTY_ERROR)
        }
        return builder.toString()
    }

    companion object {
        const val EMAIL_EMPTY_ERROR = "Fill email field"
        const val PASSWORD_MATCH_ERROR = "Passwords must match"
        const val PASSWORD_EMPTY_ERROR = "Fill password field"
        const val CONFIRM_PASSWORD_EMPTY_ERROR = "Fill confirm password field"
    }
}

