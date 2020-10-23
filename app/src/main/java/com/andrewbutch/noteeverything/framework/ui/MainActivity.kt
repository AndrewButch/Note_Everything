package com.andrewbutch.noteeverything.framework.ui

import android.os.Bundle
import android.widget.Toast
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.business.domain.util.DateUtil
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : DaggerAppCompatActivity() {

    @Inject lateinit var dateUtil: DateUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(this, dateUtil.getCurrentTimestamp(), Toast.LENGTH_SHORT).show()
    }
}