package com.andrewbutch.noteeverything.business.interactors.session

import com.andrewbutch.noteeverything.business.data.cache.abstraction.UserCacheDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class Logout
@Inject
constructor(
    private val userCacheDataSource: UserCacheDataSource
) {
    fun logout() {
        CoroutineScope(IO).launch {
            userCacheDataSource.logout()
        }
    }
}