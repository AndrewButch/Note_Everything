package com.andrewbutch.noteeverything.framework.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.framework.ui.auth.state.AuthStateEvent
import com.andrewbutch.noteeverything.framework.ui.auth.state.RegistrationFields
import com.andrewbutch.noteeverything.framework.ui.main.UIController
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_registration.*
import javax.inject.Inject


class RegistrationFragment : DaggerFragment() {

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    lateinit var viewModel: AuthViewModel

    private var email: String = ""
    private var password = ""
    private var confirmPassword = ""

    lateinit var uiController: UIController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        uiController = (context as UIController)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { navToLogin() }

        // Back button handle
        setupOnBackPressDispatcher()

        requireActivity().run {
            viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        }

        registerBtn.setOnClickListener {
            uiController.hideSoftKeyboard()
            viewModel.setRegistrationFields(RegistrationFields(
                email = emailTextView.text.toString(),
                password = passwordTextView.text.toString(),
                confirmPassword = confirmPasswordTextView.text.toString()
            ))

            viewModel.setStateEvent(AuthStateEvent.RegisterEvent(email, password, confirmPassword))
        }

        subscribeObservers()
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
                        confirmPasswordTextView.error = RegistrationFields.CONFIRM_PASSWORD_EMPTY_ERROR
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

    fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    private fun setupOnBackPressDispatcher() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                uiController.hideSoftKeyboard()
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}