package com.andrewbutch.noteeverything.framework.ui.notelistdetail

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.framework.session.SessionManager
import com.andrewbutch.noteeverything.framework.ui.BaseDetailFragment
import com.andrewbutch.noteeverything.framework.ui.main.UIController
import com.andrewbutch.noteeverything.framework.ui.notelistdetail.state.NoteListDetailStateEvent
import com.andrewbutch.noteeverything.framework.ui.notelistdetail.state.NoteListDetailViewState
import kotlinx.android.synthetic.main.fragment_note_list_detail.*
import javax.inject.Inject

class NoteListDetailFragment : BaseDetailFragment(R.layout.fragment_note_list_detail) {

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    private lateinit var viewModel: NoteListDetailViewModel

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().run {
            viewModel =
                ViewModelProvider(this, providerFactory).get(NoteListDetailViewModel::class.java)
        }
        setupUI()
        addListeners()
        subscribeObservers()

        // handle Bundle and init fields
        getNoteFromArguments()
        restoreInstanceState(savedInstanceState)
    }

    private fun setupUI() {
        // Toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { navToNotesList() }
    }

    private fun addListeners() {

        // color
        noteListColorPicker.setOnClickListener {
            val noteListColor = viewModel.getNoteListColor()
            uiController.displayColorDialog(
                initColor = noteListColor,
                callback = object : UIController.Companion.ColorDialogCallback {
                    override fun onColorChoose(color: Int) {
                        setColorInViewModel("#" + Integer.toHexString(color))
                    }
                })
        }

        // if title lost focus, set text in view model
        noteListTitle.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                setTitleInViewModel(noteListTitle.text.toString())
            }
        }

        // title change listener
        noteListTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val oldTitle = viewModel.getNoteList()?.title
                val newTitle = s.toString()
                if (newTitle.isNotBlank() && oldTitle != newTitle) {
                    viewModel.setIsPendingUpdate(true)
                }
            }
        })

        // save button
        saveBtn.setOnClickListener { onBackPressed() }

        // if user click outside input field, clear focus and hide keyboard
        noteListDetailContainer.setOnClickListener {
            clearFocus()
        }
    }

    private fun clearFocus() {
        uiController.hideSoftKeyboard()
        noteListTitle.clearFocus()
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            if (viewState != null) {
                viewState.noteList?.let { note ->
                    if (noteListTitle.text.isNullOrEmpty()) {
                        setTitle(note.title)
                    }
                    setColorPicker(note.color)
                }
            }
        }
        viewModel.shouldDisplayProgressBar()
            .observe(viewLifecycleOwner) { shouldDisplayProgressBar ->
                uiController.displayProgressBar(shouldDisplayProgressBar)
            }
    }

    private fun navToNotesList() {
        findNavController().navigate(R.id.action_noteListDetailFragment_to_notesFragment)
    }

    override fun onBackPressed() {
        if (viewModel.isPendingUpdate()) {
            setTitleInViewModel(noteListTitle.text.toString())
            viewModel.getNoteList()?.let { noteList ->
                viewModel.setStateEvent(
                    NoteListDetailStateEvent.UpdateNoteListEvent(
                        noteList = noteList,
                        user = sessionManager.authUser.value!!
                    )
                )
            }
        }
        uiController.hideSoftKeyboard()
        findNavController().popBackStack()
    }

    private fun setTitle(title: String) {
        noteListTitle.text?.clear()
        noteListTitle.text?.append(title)
    }

    private fun setColorPicker(color: String) {
        noteListColorPicker.background.setColorFilter(
            Color.parseColor(color),
            PorterDuff.Mode.MULTIPLY
        )
    }

    private fun setTitleInViewModel(title: String) {
        viewModel.setNoteListTitle(title)
    }

    private fun setColorInViewModel(color: String) {
        viewModel.setNoteColor(color)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_note_list_detail_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_notelist -> {
                viewModel.getNoteList()?.let { noteList ->
                    viewModel.setStateEvent(
                        NoteListDetailStateEvent.DeleteNoteListEvent(
                            noteList = noteList,
                            user = sessionManager.authUser.value!!
                        )
                    )
                    findNavController().popBackStack()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getNoteFromArguments() {
        arguments?.let { args ->
            val note = args.getParcelable(NOTE_LIST_DETAIL_BUNDLE_KEY) as NoteList?
            note?.let {
                viewModel.setNoteList(it)
                args.clear()
            }
        }
    }

    private fun restoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let { args ->
            val viewState = args.getParcelable(VIEW_STATE) as NoteListDetailViewState?
            viewState?.let {
                viewModel.setViewState(it)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(VIEW_STATE, viewModel.viewState.value)
    }

    companion object {
        const val VIEW_STATE =
            "com.andrewbutch.noteeverything.framework.ui.notelistdetail.VIEW_STATE"
        const val NOTE_LIST_DETAIL_BUNDLE_KEY = "NotesFragment.NOTE_LIST_DETAIL_BUNDLE_KEY"

    }
}