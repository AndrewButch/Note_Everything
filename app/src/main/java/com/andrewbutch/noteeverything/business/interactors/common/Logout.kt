package com.andrewbutch.noteeverything.business.interactors.common

import com.andrewbutch.noteeverything.business.data.network.abstraction.UserNetworkDataSource
import javax.inject.Inject

class Logout
@Inject
constructor(
    private val userNetworkDataSource: UserNetworkDataSource
) {
}