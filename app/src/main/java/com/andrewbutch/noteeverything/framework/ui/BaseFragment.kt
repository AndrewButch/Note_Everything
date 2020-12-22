package com.andrewbutch.noteeverything.framework.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import com.andrewbutch.noteeverything.framework.ui.main.UIController
import dagger.android.support.DaggerFragment

abstract class BaseFragment(
    @LayoutRes private val layoutId: Int
) : DaggerFragment() {
    lateinit var uiController: UIController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        uiController = (context as UIController)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupOnBackPressDispatcher()
    }

    // Back button callback
    private fun setupOnBackPressDispatcher() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    abstract fun onBackPressed()
}