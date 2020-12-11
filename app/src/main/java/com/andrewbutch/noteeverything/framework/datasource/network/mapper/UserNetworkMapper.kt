package com.andrewbutch.noteeverything.framework.datasource.network.mapper

import com.andrewbutch.noteeverything.business.domain.model.User
import com.google.firebase.auth.FirebaseUser

class UserNetworkMapper {

    fun mapFromNetwork(firebaseUser: FirebaseUser) : User {
        return User(
            id = firebaseUser.uid,
            displayName = firebaseUser.displayName,
            email = firebaseUser.email)
    }
}