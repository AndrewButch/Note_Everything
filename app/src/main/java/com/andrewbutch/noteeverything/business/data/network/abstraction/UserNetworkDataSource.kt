package com.andrewbutch.noteeverything.business.data.network.abstraction

import com.andrewbutch.noteeverything.business.domain.model.User

interface UserNetworkDataSource {

    suspend fun login(email: String, password: String): User?

    suspend fun registration(email: String, password: String): User?

}