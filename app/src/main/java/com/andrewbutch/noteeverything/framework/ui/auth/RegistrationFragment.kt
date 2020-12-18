package com.andrewbutch.noteeverything.framework.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.framework.ui.auth.state.AuthStateEvent
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

        confirmPasswordTextView.addTextChangedListener { newPassword ->
            newPassword?.let {
                checkAndSavePassword(newPassword.toString())
            }
        }

        registerBtn.setOnClickListener {
            if (checkAndSaveEmail(emailTextView.text.toString()) &&
                checkAndSavePassword(passwordTextView.text.toString()) &&
                checkAndSaveConfirmPassword(confirmPasswordTextView.text.toString())
            )
            viewModel.setStateEvent(AuthStateEvent.RegisterEvent(email, password, confirmPassword))
        }
    }

    private fun navToLogin() {
        findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
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

    fun checkAndSaveConfirmPassword(newPassword: String): Boolean {
        if (isPasswordCorrect(newPassword)) {
            if (newPassword != confirmPassword) {
                confirmPassword = newPassword
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