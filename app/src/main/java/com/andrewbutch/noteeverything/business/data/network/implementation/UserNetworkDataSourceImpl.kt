package com.andrewbutch.noteeverything.business.data.network.implementation

import com.andrewbutch.noteeverything.business.data.network.abstraction.UserNetworkDataSource
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.AuthFirestoreService
import javax.inject.Inject

class UserNetworkDataSourceImpl
@Inject
constructor(private val authService: AuthFirestoreService) : UserNetworkDataSource {

    override suspend fun login(email: String, password: String): User? {
        return authService.login(email, password)
    }

    override suspend fun registration(
        email: String,
        password: String,
    ): User? {
        return authService.register(email, password)
    }
}