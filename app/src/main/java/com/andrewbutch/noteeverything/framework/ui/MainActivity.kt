package com.andrewbutch.noteeverything.framework.ui

import android.os.Bundle
import android.view.Menu
import com.andrewbutch.noteeverything.R
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

//    override fun onSupportNavigateUp(): Boolean {
//        return NavigationUI.navigateUp(findNavController(R.id.navHostFragmentContainer), drawer)
//    }
//
//    override fun onBackPressed() {
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START)
//        } else {
//            super.onBackPressed()
//        }
//    }




}