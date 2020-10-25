package com.andrewbutch.noteeverything.framework.ui.notedetail

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.framework.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_note_detail.*

class NoteDetailFragment : BaseFragment(
    R.layout.fragment_note_detail) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        saveBtn.setOnClickListener { navToNotesList() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_note_detail_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_note -> {
                showToast("Delete note")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupUI() {
        // Toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { navToNotesList() }

        // handle Bundle and init fields
        getSelectedNoteListFromArguments()
    }

    private fun getSelectedNoteListFromArguments() {
        arguments?.let { args ->
            val noteList = args.getParcelable("NOTE_DETAIL_SELECTED_NOTE_BUNDLE_KEY") as Note?
            noteList?.let {
                noteTitle.setText(it.title, TextView.BufferType.EDITABLE)
                noteColorPicker.background.setColorFilter(
                    Color.parseColor(it.color),
                    PorterDuff.Mode.MULTIPLY
                )
            }
        }
    }





}