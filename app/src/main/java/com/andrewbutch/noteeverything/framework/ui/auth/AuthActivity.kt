package com.andrewbutch.noteeverything.framework.ui.auth

import android.content.Intent
import android.os.Bundle
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.framework.ui.main.MainActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


class AuthActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }


    @FlowPreview
    @ExperimentalCoroutinesApi
    fun navToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}