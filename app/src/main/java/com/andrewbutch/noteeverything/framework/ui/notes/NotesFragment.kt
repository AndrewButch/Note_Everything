package com.andrewbutch.noteeverything.framework.ui.notes

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.interactors.common.DeleteNoteList
import com.andrewbutch.noteeverything.framework.ui.main.UIController
import com.andrewbutch.noteeverything.framework.ui.main.UIController.Companion.InputDialogCallback
import com.andrewbutch.noteeverything.framework.ui.notedetail.NoteDetailFragment.Companion.NOTE_DETAIL_BUNDLE_KEY
import com.andrewbutch.noteeverything.framework.ui.notelistdetail.NoteListDetailFragment.Companion.NOTE_LIST_DETAIL_BUNDLE_KEY
import com.andrewbutch.noteeverything.framework.ui.notes.drawer.NavMenuAdapter
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListStateEvent
import com.andrewbutch.noteeverything.framework.ui.utils.VerticalItemDecoration
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_notes.*
import kotlinx.android.synthetic.main.layout_fragment_notes_content.*
import kotlinx.android.synthetic.main.nav_header.*
import timber.log.Timber
import javax.inject.Inject

class NotesFragment :
    DaggerFragment(),
    NotesRecyclerAdapter.Interaction,
    NavMenuAdapter.Interaction {

    lateinit var viewModel: NotesViewModel
    private lateinit var navMenuAdapter: NavMenuAdapter
    private lateinit var notesAdapter: NotesRecyclerAdapter
    private lateinit var uiController: UIController

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences


    override fun onAttach(context: Context) {
        super.onAttach(context)
        uiController = (context as UIController)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(viewModelStore, providerFactory).get(NotesViewModel::class.java)
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupNavDrawer()
        // FAB
        notesFragmentFab.setOnClickListener {
            showInputDialog("Новая заметка", InputDialogType.NOTE)
        }

        subscribeObservers()
        setupOnBackPressDispatcher()
    }


    private fun showInputDialog(title: String, type: InputDialogType) {
        uiController.displayInputDialog(
            title,
            object : InputDialogCallback {
                override fun onInputComplete(text: String) {
                    when (type) {
                        InputDialogType.NOTE -> {
                            viewModel.getSelectedNoteList()?.let { selectedNoteList ->
                                viewModel.setStateEvent(
                                    NoteListStateEvent.InsertNewNoteEvent(
                                        title = text,
                                        listId = selectedNoteList.id

                                    )
                                )
                            }
                        }
                        InputDialogType.LIST -> {
                            viewModel.setStateEvent(
                                NoteListStateEvent.InsertNewNoteListEvent(text)
                            )
                        }
                    }

                }

            })
    }

    private fun subscribeObservers() {
        // Observe view state
        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            if (viewState != null) {
                viewState.notes?.let {
                    notesAdapter.submitList(it)
                }

                viewState.noteLists?.let {
                    navMenuAdapter.submitList(it)
                    if (viewState.selectedNoteList == null) {
                        extractFromPreferences()
                    }
                }

                viewState.newNote?.let {
                    navToNoteDetail(it)
                }

                viewState.newNoteList?.let {
                    navToNoteListDetail(it)
                }

                viewState.selectedNoteList?.let { noteList ->
                    showNotesContainer()
                    setTitle(noteList.title)
                }
                Timber.i("Selected list: ${viewState.selectedNoteList}")
            }
        }
        viewModel.getStateMessage().observe(viewLifecycleOwner) { stateMessage ->
            if (stateMessage != null) {
                stateMessage.message?.let { message ->
                    if (message == DeleteNoteList.DELETE_NOTE_LIST_SUCCESS) {
                        hideNotesContainer()
                        viewModel.setSelectedList(null)
                        notesAdapter.submitList(emptyList())

                    }
                    viewModel.removeStateMessage()
                }
            }
        }

        viewModel.shouldDisplayProgressBar()
            .observe(viewLifecycleOwner) { shouldDisplayProgressBar ->
                uiController.displayProgressBar(shouldDisplayProgressBar)
            }
    }

    private fun setTitle(title: String) {
        toolbar.title = title
    }

    private fun setupViews() {
        // Toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        hideNotesContainer()
    }

    private fun setupOnBackPressDispatcher() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setupNavDrawer() {
        // Toggle listener
        val toggle = ActionBarDrawerToggle(
            requireActivity(),
            drawer,
            toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        // button "add list"
        addNoteListBtn.setOnClickListener {
            showInputDialog("Новый список", InputDialogType.LIST)
        }

        // recycler view
        navMenuAdapter = NavMenuAdapter(interaction = this)
        navRecyclerMenu.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = navMenuAdapter
        }
        navRecyclerMenu.addItemDecoration(VerticalItemDecoration(20))
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        notesAdapter = NotesRecyclerAdapter(interaction = this)
        recycler.apply {
            adapter = notesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        recycler.addItemDecoration(VerticalItemDecoration(30))
        viewModel.setStateEvent(NoteListStateEvent.GetAllNoteListsEvent())

        viewModel.insertTestData()

        extractFromPreferences()
    }

    private fun extractFromPreferences() {
        val selectedNoteList = preferences.getString(SELECTED_NOTE, null)
        selectedNoteList?.let {
            viewModel.setSelectedList(selectedNoteList)
        }
    }

    private fun setPreferences() {
        val editor = preferences.edit()
        editor.putString(SELECTED_NOTE, viewModel.getSelectedNoteList()?.id)
        editor.apply()
    }

    // Note clicked
    override fun onItemSelected(position: Int, item: Note) {
        navToNoteDetail(item)
    }

    // List clicked
    override fun onItemSelected(position: Int, item: NoteList) {
        drawer.closeDrawer(GravityCompat.START)
        viewModel.setStateEvent(NoteListStateEvent.SelectNoteListEvent(item))
    }

    // List long click
    override fun onLongClick(position: Int, item: NoteList) {
        navToNoteListDetail(item)
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun navToNoteDetail(selectedNote: Note) {
        val bundle = bundleOf(NOTE_DETAIL_BUNDLE_KEY to selectedNote)

        findNavController().navigate(
            R.id.action_notesFragment_to_noteDetailFragment,
            bundle
        )
        drawer.closeDrawer(GravityCompat.START)
        viewModel.setNewNote(null)
    }

    private fun navToNoteListDetail(selectedNoteList: NoteList) {
        val bundle = bundleOf(NOTE_LIST_DETAIL_BUNDLE_KEY to selectedNoteList)

        findNavController().navigate(
            R.id.action_notesFragment_to_noteListDetailFragment,
            bundle
        )
        drawer.closeDrawer(GravityCompat.START)
        viewModel.setNewNoteList(null)
    }

    private fun hideNotesContainer() {
        notes_container.visibility = View.GONE
        notesFragmentFab.visibility = View.GONE
    }

    private fun showNotesContainer() {
        notes_container.visibility = View.VISIBLE
        notesFragmentFab.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        viewModel.reloadNoteLists()
        viewModel.reloadListItems()
    }

    override fun onPause() {
        super.onPause()
        setPreferences()
    }


    enum class InputDialogType {
        NOTE, LIST
    }

    companion object {
        const val SELECTED_NOTE = "com.andrewbutch.noteeverything.framework.ui.notes.SELECTED_NOTE"
    }

}