package com.andrewbutch.noteeverything.framework.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.framework.ui.auth.state.AuthStateEvent
import com.andrewbutch.noteeverything.framework.ui.auth.state.LoginFields
import com.andrewbutch.noteeverything.framework.ui.main.UIController
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class LoginFragment : DaggerFragment() {

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    lateinit var viewModel: AuthViewModel

    private lateinit var uiController: UIController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        uiController = (context as UIController)
    }

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

        // Login button
        loginBtn.setOnClickListener {
            uiController.hideSoftKeyboard()
            viewModel.setLoginFields(
                LoginFields(
                    email = emailTextView.text.toString(),
                    password = passwordTextView.text.toString()
                )
            )

            viewModel.setStateEvent(
                AuthStateEvent.LoginEvent(
                    email = emailTextView.text.toString(),
                    password = passwordTextView.text.toString()
                )
            )
        }

        // Registration button
        registrationBtn.setOnClickListener {
            navToRegistration()
        }

        // Forgot password button
        forgotPassBtn.setOnClickListener {
            navToForgotPassword()
        }

        checkPreviousAuthUser()

        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.getStateMessage().observe(viewLifecycleOwner) { stateMessage ->
            if (stateMessage != null) {
                stateMessage.message?.let { message ->
                    if (message.contains(LoginFields.EMAIL_EMPTY_ERROR)) {
                        emailTextView.error = LoginFields.EMAIL_EMPTY_ERROR
                    }
                    if (message.contains(LoginFields.PASSWORD_EMPTY_ERROR)) {
                        passwordTextView.error = LoginFields.PASSWORD_EMPTY_ERROR
                    }
                }
                viewModel.removeStateMessage()
            }
        }

        viewModel.shouldDisplayProgressBar().observe(viewLifecycleOwner) { displayProgressBar ->
            uiController.displayProgressBar(displayProgressBar)
        }
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

    fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}