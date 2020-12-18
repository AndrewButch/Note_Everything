package com.andrewbutch.noteeverything.business.interactors.auth

import com.andrewbutch.noteeverything.business.interactors.session.PreviousSession

class AuthInteractors
constructor(
    val login: Login,
    val registration: Registration,
    val previousSession: PreviousSession
) {
}