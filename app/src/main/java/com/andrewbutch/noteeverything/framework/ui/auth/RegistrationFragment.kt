package com.andrewbutch.noteeverything.framework.ui.auth

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.framework.ui.BaseFragment
import com.andrewbutch.noteeverything.framework.ui.auth.state.AuthStateEvent
import com.andrewbutch.noteeverything.framework.ui.auth.state.RegistrationFields
import kotlinx.android.synthetic.main.fragment_registration.*
import javax.inject.Inject


class RegistrationFragment : BaseFragment(R.layout.fragment_registration) {

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    lateinit var viewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { navToLogin() }

        requireActivity().run {
            viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        }

        registerBtn.setOnClickListener {
            uiController.hideSoftKeyboard()
            viewModel.setRegistrationFields(
                RegistrationFields(
                    email = emailTextView.text.toString(),
                    password = passwordTextView.text.toString(),
                    confirmPassword = confirmPasswordTextView.text.toString()
                )
            )

            viewModel.setStateEvent(
                AuthStateEvent.RegisterEvent(
                    email = emailTextView.text.toString(),
                    password = passwordTextView.text.toString(),
                    confirmPassword = confirmPasswordTextView.text.toString()
                )
            )
        }

        subscribeObservers()
    }

    override fun onBackPressed() {
        uiController.hideSoftKeyboard()
        findNavController().popBackStack()
    }

    private fun subscribeObservers() {
        viewModel.getStateMessage().observe(viewLifecycleOwner) { stateMessage ->
            if (stateMessage != null) {
                stateMessage.message?.let { message ->
                    if (message.contains(RegistrationFields.EMAIL_EMPTY_ERROR)) {
                        emailTextView.error = RegistrationFields.EMAIL_EMPTY_ERROR
                    }
                    if (message.contains(RegistrationFields.PASSWORD_MATCH_ERROR)) {
                        passwordTextView.error = RegistrationFields.PASSWORD_MATCH_ERROR
                        confirmPasswordTextView.error = RegistrationFields.PASSWORD_MATCH_ERROR
                    }
                    if (message.contains(RegistrationFields.PASSWORD_EMPTY_ERROR)) {
                        passwordTextView.error = RegistrationFields.PASSWORD_EMPTY_ERROR

                    }
                    if (message.contains(RegistrationFields.CONFIRM_PASSWORD_EMPTY_ERROR)) {
                        confirmPasswordTextView.error =
                            RegistrationFields.CONFIRM_PASSWORD_EMPTY_ERROR
                    }
                }
                viewModel.removeStateMessage()
            }
        }

        viewModel.shouldDisplayProgressBar().observe(viewLifecycleOwner) { displayProgressBar ->
            uiController.displayProgressBar(displayProgressBar)
        }
    }

    private fun navToLogin() {
        findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
    }
}