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
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            return EMPTY_FIELDS_ERROR
        }
        return ""
    }

    companion object {
        const val EMPTY_FIELDS_ERROR = "Can`t login without email and password"
    }
}

data class RegistrationFields(
    val email: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null
) {

    fun isValidRegistration(): String {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            return EMPTY_FIELDS_ERROR
        }

        if (password != confirmPassword) {
            return PASSWORD_MATCH_ERROR
        }

        return ""
    }

    companion object {
        const val EMPTY_FIELDS_ERROR = "Fill all fields"
        const val PASSWORD_MATCH_ERROR = "Passwords must match"
    }
}

