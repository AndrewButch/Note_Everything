package com.andrewbutch.noteeverything.business.data.cache.abstraction

import com.andrewbutch.noteeverything.business.domain.model.User

interface UserCacheDataSource {

    suspend fun getPreviousUser(): User?
}