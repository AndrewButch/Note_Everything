package com.andrewbutch.noteeverything.framework.datasource.network.mapper

import com.andrewbutch.noteeverything.business.domain.model.User
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject


class UserNetworkMapper
@Inject constructor() {

    fun mapFromNetwork(firebaseUser: FirebaseUser): User {
        return User(
            id = firebaseUser.uid,
            displayName = firebaseUser.displayName,
            email = firebaseUser.email
        )
    }
}