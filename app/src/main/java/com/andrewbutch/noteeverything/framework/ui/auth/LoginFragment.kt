package com.andrewbutch.noteeverything.framework.ui.auth

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.framework.session.SessionManager
import com.andrewbutch.noteeverything.framework.ui.BaseFragment
import com.andrewbutch.noteeverything.framework.ui.auth.state.AuthStateEvent
import com.andrewbutch.noteeverything.framework.ui.auth.state.LoginFields
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class LoginFragment : BaseFragment(R.layout.fragment_login) {

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    lateinit var viewModel: AuthViewModel

    @Inject
    lateinit var sessionManager: SessionManager

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



        subscribeObservers()
    }

    override fun onBackPressed() {
        requireActivity().finish()
    }

    private fun subscribeObservers() {
        viewModel.getStateMessage().observe(viewLifecycleOwner) { stateMessage ->
            if (stateMessage != null) {
                stateMessage.message?.let { message ->
                    if (message.contains(LoginFields.EMAIL_EMPTY_ERROR)) {
                        emailTextView.error = resources.getString(R.string.empty_email_error)
                    }
                    if (message.contains(LoginFields.PASSWORD_EMPTY_ERROR)) {
                        passwordTextView.error = resources.getString(R.string.empty_password_error)
                    }
                }
                viewModel.removeStateMessage()
            }
        }
        viewModel.shouldDisplayProgressBar().observe(viewLifecycleOwner) { displayProgressBar ->
            uiController.displayProgressBar(displayProgressBar)
        }
        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            viewState.user?.let {
                sessionManager.login(user = it)
            }
        }
    }

    private fun navToRegistration() {
        findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
    }


}