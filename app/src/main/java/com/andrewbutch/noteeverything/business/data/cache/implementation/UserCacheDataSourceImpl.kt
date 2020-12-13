package com.andrewbutch.noteeverything.business.data.cache.implementation

import com.andrewbutch.noteeverything.business.data.cache.abstraction.UserCacheDataSource
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.AuthFirestoreService
import javax.inject.Inject

class UserCacheDataSourceImpl
@Inject
constructor(
    private val authService: AuthFirestoreService
) : UserCacheDataSource {

    override suspend fun getPreviousUser(): User? {
        return authService.getCurrentUser()
    }
}