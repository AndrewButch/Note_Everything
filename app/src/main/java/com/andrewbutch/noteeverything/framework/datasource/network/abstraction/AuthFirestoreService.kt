package com.andrewbutch.noteeverything.framework.datasource.network.abstraction

import com.andrewbutch.noteeverything.business.domain.model.User

interface AuthFirestoreService {

    suspend fun login(email: String, password: String): User?

    suspend fun register(email: String, password: String): User?

    suspend fun getCurrentUser(): User?

    suspend fun logout()
}