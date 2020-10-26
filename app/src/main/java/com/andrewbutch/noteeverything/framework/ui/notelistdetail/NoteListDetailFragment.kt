package com.andrewbutch.noteeverything.framework.ui.notelistdetail

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.framework.ui.BaseDetailFragment
import kotlinx.android.synthetic.main.fragment_note_list_detail.*

class NoteListDetailFragment : BaseDetailFragment(R.layout.fragment_note_list_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        saveBtn.setOnClickListener { navToNotesList() }
    }

    private fun setupUI() {
        // Toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { navToNotesList() }

        // handle Bundle and init fields
        getSelectedNoteListFromArguments()

        addListeners()
    }

    private fun addListeners() {
        noteListColorPicker.setOnClickListener { showToast("Color picker clicked") }
    }

    private fun getSelectedNoteListFromArguments() {
        arguments?.let { args ->
            val noteList =
                args.getParcelable("NOTE_LIST_DETAIL_SELECTED_NOTE_BUNDLE_KEY") as NoteList?
            noteList?.let {
                noteListTitle.setText(it.title, TextView.BufferType.EDITABLE)
                noteListColorPicker.background.setColorFilter(
                    Color.parseColor(it.color),
                    PorterDuff.Mode.MULTIPLY
                )
            }
        }
    }

    private fun navToNotesList() {
        findNavController().navigate(R.id.action_noteListDetailFragment_to_notesFragment)
    }
}