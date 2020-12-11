package com.andrewbutch.noteeverything.framework.ui.auth

import android.os.Bundle
import android.widget.Toast
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.AuthFirestoreService
import com.andrewbutch.noteeverything.framework.datasource.network.implementation.AuthFireStoreServiceImpl
import com.andrewbutch.noteeverything.framework.datasource.network.mapper.UserNetworkMapper
import com.google.firebase.auth.FirebaseAuth
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivity : DaggerAppCompatActivity() {

    lateinit var authService: AuthFirestoreService
    val email = "testuser2@mail.ru"
    val pass = "123456"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        authService = AuthFireStoreServiceImpl(FirebaseAuth.getInstance(), UserNetworkMapper())

        loginBtn.setOnClickListener {
            CoroutineScope(IO).launch {
                val user = authService.login(email, pass)
                user?.let {
                    setUser(user)
                }
            }
        }

        checkSessionBtn.setOnClickListener {
            val authUser = authService.getCurrentUser()
            if (authUser != null) {
                Toast.makeText(this, "Cached user", Toast.LENGTH_SHORT).show()
                CoroutineScope(Main).launch {
                    setUser(authUser)
                }
            } else {
                Toast.makeText(this, "No auth user", Toast.LENGTH_SHORT).show()
            }
        }

        logoutBtn.setOnClickListener {
            Toast.makeText(this, "Try logout", Toast.LENGTH_SHORT).show()
            CoroutineScope(IO).launch {
                authService.logout()
                cleanUser()

            }
        }
    }

    suspend fun setUser(user: User) = withContext(Main) {
        userTextView.text = "name: ${user.displayName}\n" +
                "email: ${user.email}\n " +
                "user: ${user.id}"
    }

    suspend fun cleanUser() = withContext(Main) {
        userTextView.text = ""
    }
}