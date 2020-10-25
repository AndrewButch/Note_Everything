package com.andrewbutch.noteeverything.framework.ui.notedetail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.business.domain.model.Note
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_note_detail.*

class NoteDetailFragment : DaggerFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_note_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getSelectedNoteListFromArguments()
        saveBtn.setOnClickListener { navToNotesList() }
    }

    private fun getSelectedNoteListFromArguments() {
        arguments?.let { args ->
            val noteList = args.getParcelable("NOTE_DETAIL_SELECTED_NOTE_BUNDLE_KEY") as Note?
            noteList?.let {
                noteTitle.setText(it.title, TextView.BufferType.EDITABLE)
                noteColorPicker.setBackgroundColor(Color.parseColor(it.color))
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun navToNotesList() {
        findNavController().navigate(R.id.action_noteDetailFragment_to_notesFragment)
    }
}