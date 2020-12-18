package com.andrewbutch.noteeverything.framework.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.framework.ui.auth.state.AuthStateEvent
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class LoginFragment : DaggerFragment() {

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    lateinit var viewModel: AuthViewModel

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

        requireActivity().run {
            viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        }

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
                checkAndSavePassword(passwordTextView.text.toString())
            )

                viewModel.setStateEvent(AuthStateEvent.LoginEvent(email, password))
        }

        registrationBtn.setOnClickListener {
//            if (checkAndSaveEmail(emailTextView.text.toString()) &&
//                checkAndSavePassword(passwordTextView.text.toString())
//            )
//
//                viewModel.setStateEvent(AuthStateEvent.RegisterEvent(email, password, password))
            navToRegistration()
        }

        forgotPassBtn.setOnClickListener { navToForgotPassword() }

        checkPreviousAuthUser()

    }

    private fun navToForgotPassword() {
        showToast("Восстановление пароля")
    }

    private fun navToRegistration() {
        showToast("Регистрация")
        findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
    }

    fun checkPreviousAuthUser() {
        viewModel.setStateEvent(AuthStateEvent.CheckPreviousAuth)
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