package com.andrewbutch.noteeverything.framework.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.AuthFirestoreService
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginFragment : DaggerFragment() {

    @Inject
    lateinit var authService: AuthFirestoreService
    private var email: String = ""
    private var password = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailTextView.addTextChangedListener { newEmail ->
            newEmail?.let {
                checkAndSaveEmail(newEmail.toString())
            }
        }

        passwordTextView.addTextChangedListener { newPassword ->
            newPassword?.let {
                checkAndSavePassword(newPassword.toString())
            }
        }

        loginBtn.setOnClickListener {
            if (checkAndSaveEmail(emailTextView.text.toString()) &&
                checkAndSavePassword(passwordTextView.text.toString()))

                CoroutineScope(Dispatchers.IO).launch {
                    val user = authService.login(email, password)
                    user?.let {
                        setUser(user)
                        (requireActivity() as AuthActivity).navToMain()
                    }
                }
        }

        registrationBtn.setOnClickListener { navToRegistration() }

        forgotPassBtn.setOnClickListener { navToForgotPassword() }

        checkAuthBtn.setOnClickListener { checkPreviousAuthUser() }

        logoutBtn.setOnClickListener { logout() }
    }

    private fun navToForgotPassword() {
        showToast("Восстановление пароля")
    }

    private fun navToRegistration() {
        showToast("Регистрация")
    }

    suspend fun setUser(user: User) = withContext(Dispatchers.Main) {
        userInfo.text = "name: ${user.displayName}\n" +
                "email: ${user.email}\n " +
                "user: ${user.id}"
    }

    suspend fun cleanUser() = withContext(Dispatchers.Main) {
        userInfo.text = "Session Info"
    }

    fun checkPreviousAuthUser() {
        val authUser = authService.getCurrentUser()
        if (authUser != null) {
            showToast("Cached user")
            CoroutineScope(Dispatchers.Main).launch {
                setUser(authUser)
            }
        } else {
            showToast("No auth user")

        }
    }

    fun logout() {
        showToast("Try logout")
        CoroutineScope(Dispatchers.IO).launch {
            authService.logout()
            cleanUser()
        }
    }

    fun checkAndSaveEmail(newEmail: String): Boolean {
        if (isEmailCorrect(newEmail)) {
            if (newEmail != email) {
                email = newEmail
            }
            return true
        } else {
            showToast("Incorrect email")
        }
        return false
    }

    fun checkAndSavePassword(newPassword: String): Boolean {
        if (isPasswordCorrect(newPassword)) {
            if (newPassword != password) {
                password = newPassword
            }
            return true
        } else {
            showToast("Incorrect password")
        }
        return false
    }

    private fun isEmailCorrect(newEmail: String): Boolean {
        return newEmail.isNotEmpty() &&
                newEmail.isNotBlank()

    }

    private fun isPasswordCorrect(newPassword: String): Boolean {
        return newPassword.isNotEmpty() &&
                newPassword.isNotBlank()
    }

    fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}