package com.andrewbutch.noteeverything.framework.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.framework.session.SessionManager
import com.andrewbutch.noteeverything.framework.ui.main.MainActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


class AuthActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(this) { viewState ->
            viewState.user?.let {
                sessionManager.login(user = it)
            }
        }

        sessionManager.authUser.observe(this) { user ->
            user?.let {
                if (it.id.isNotEmpty()) {
                    navToMain()
                    Toast.makeText(this, "${user.email}", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }


    @FlowPreview
    @ExperimentalCoroutinesApi
    fun navToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}