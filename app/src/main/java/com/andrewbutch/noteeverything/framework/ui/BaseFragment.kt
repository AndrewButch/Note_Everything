package com.andrewbutch.noteeverything.framework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.navigation.fragment.findNavController
import com.andrewbutch.noteeverything.R
import dagger.android.support.DaggerFragment

open class BaseFragment(
    @LayoutRes private val layoutId: Int
) : DaggerFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }

    fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    fun navToNotesList() {
        findNavController().navigate(R.id.action_noteDetailFragment_to_notesFragment)
    }
}