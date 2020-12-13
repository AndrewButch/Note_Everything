package com.andrewbutch.noteeverything.framework.session

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.AuthFirestoreService
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    val authFirestoreService: AuthFirestoreService
) {

    private val _authUser: MutableLiveData<User> = MutableLiveData()
    val authUser: LiveData<User>
        get() = _authUser

    fun login(user: User) {
        Timber.d("Login $user")
        setValue(user)
    }

    fun logout() {
        Timber.d("Logout")
        GlobalScope.launch(Main) {
            authFirestoreService.logout()
            setValue(null)
        }
    }

    private fun setValue(newValue: User?) {
        GlobalScope.launch(Main) {
            if (_authUser.value != newValue) {
                _authUser.value = newValue
            }
        }
    }
}