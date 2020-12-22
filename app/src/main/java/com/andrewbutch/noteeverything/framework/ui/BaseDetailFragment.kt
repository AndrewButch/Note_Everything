package com.andrewbutch.noteeverything.framework.ui

import android.os.Bundle
import androidx.annotation.LayoutRes

abstract class BaseDetailFragment(
    @LayoutRes private val layoutId: Int
) : BaseFragment(layoutId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

}