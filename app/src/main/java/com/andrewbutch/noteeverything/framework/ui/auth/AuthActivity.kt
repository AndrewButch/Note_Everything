package com.andrewbutch.noteeverything.framework.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.framework.session.SessionManager
import com.andrewbutch.noteeverything.framework.ui.main.MainActivity
import com.andrewbutch.noteeverything.framework.ui.main.UIController
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class AuthActivity : DaggerAppCompatActivity(), UIController {

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
        sessionManager.authUser.observe(this) { user ->
            user?.let {
                if (it.id.isNotEmpty()) {
                    navToMain()
                }
            }
        }

        viewModel.shouldDisplayProgressBar()
            .observe(this) { shouldDisplayProgressBar ->
                displayProgressBar(shouldDisplayProgressBar)
            }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    fun navToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(isDisplayed: Boolean) {
        if (isDisplayed) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    override fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager
                .hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    override fun displayInputDialog(
        title: String,
        callback: UIController.Companion.InputDialogCallback
    ) {
        TODO("Not yet implemented")
    }

    override fun displayColorDialog(
        initColor: Int?,
        callback: UIController.Companion.ColorDialogCallback
    ) {
        TODO("Not yet implemented")
    }

    override fun displayInfoDialog(title: String, message: String) {
        TODO("Not yet implemented")
    }

    override fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}