package com.andrewbutch.noteeverything.framework.ui.notedetail

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.framework.ui.BaseDetailFragment
import com.andrewbutch.noteeverything.framework.ui.main.UIController
import com.andrewbutch.noteeverything.framework.ui.notedetail.state.NoteDetailStateEvent
import com.andrewbutch.noteeverything.framework.ui.notes.NotesFragment
import kotlinx.android.synthetic.main.fragment_note_detail.*
import javax.inject.Inject

class NoteDetailFragment : BaseDetailFragment(R.layout.fragment_note_detail) {
    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    private val viewModel: NoteDetailViewModel by viewModels { providerFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        setupOnBackPressDispatcher()
        addListeners()
        subscribeObservers()

        // handle Bundle and init fields
        getNoteFromArguments()
    }

    private fun setupUI() {
        // Toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun addListeners() {
        // complete switch
        switchComplete.setOnClickListener { setCompletedInViewModel(switchComplete.isChecked) }

        // color
        noteColorPicker.setOnClickListener {
            val noteColor = viewModel.getNoteColor()
            uiController.displayColorDialog(
                initColor = noteColor,
                callback = object : UIController.Companion.ColorDialogCallback {
                    override fun onColorChoose(color: Int) {
                        setColorInViewModel("#" + Integer.toHexString(color))
                    }
                })
        }
        // save button
        saveBtn.setOnClickListener { onBackPressed() }

        // if title lost focus, set text in view model
        noteTitle.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                setTitleInViewModel(noteTitle.text.toString())
            }
        }
        // title change listener
        noteTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val oldTitle = viewModel.getNote()?.title
                val newTitle = s.toString()
                if (newTitle.isNotBlank() && oldTitle != newTitle) {
                    viewModel.setIsPendingUpdate(true)
                }
            }
        })

        // if user click outside input field, clear focus and hide keyboard
        noteDetailContainer.setOnClickListener {
            clearFocus()
        }
    }

    private fun clearFocus() {
        uiController.hideSoftKeyboard()
        noteTitle.clearFocus()
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            if (viewState != null) {
                viewState.note?.let { note ->
                    if (noteTitle.text.isNullOrEmpty()) {
                        setTitle(note.title)
                    }
                    setColor(note.color)
                    setCompleted(note.completed)
                }
            }
        }
    }

    private fun setTitle(title: String) {
        noteTitle.text?.clear()
        noteTitle.text?.append(title)
    }

    private fun setColor(color: String) {
        noteColorPicker.background.setColorFilter(
            Color.parseColor(color),
            PorterDuff.Mode.MULTIPLY
        )
    }

    private fun setCompleted(completed: Boolean) {
        switchComplete.isChecked = completed
    }

    private fun getNoteFromArguments() {
        arguments?.let { args ->
            val note = args.getParcelable(NotesFragment.NOTE_DETAIL_BUNDLE_KEY) as Note?
            note?.let {
                viewModel.setNote(it)
            }
        }
    }

    override fun onBackPressed() {
        if (viewModel.isPendingUpdate()) {
            setTitleInViewModel(noteTitle.text.toString())
            viewModel.setStateEvent(
                NoteDetailStateEvent.UpdateNoteEvent()
            )
        }
        uiController.hideSoftKeyboard()
        findNavController().popBackStack()
    }


    private fun setTitleInViewModel(title: String) {
        viewModel.setNoteTitle(title)
    }

    private fun setColorInViewModel(color: String) {
        viewModel.setNoteColor(color)
    }

    private fun setCompletedInViewModel(completed: Boolean) {
        viewModel.setNoteCompleted(completed)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_note_detail_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_note -> {
                viewModel.getNote()?.let {
                    viewModel.setStateEvent(
                        NoteDetailStateEvent.DeleteNoteEvent(it)
                    )
                    findNavController().popBackStack()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}