package com.andrewbutch.noteeverything.framework.ui.notes

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.andrewbutch.noteeverything.framework.ui.main.UIController
import com.andrewbutch.noteeverything.framework.ui.notes.drawer.NavMenuAdapter
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListStateEvent
import com.andrewbutch.noteeverything.framework.ui.utils.VerticalItemDecoration
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_notes.*
import kotlinx.android.synthetic.main.layout_fragment_notes_content.*
import kotlinx.android.synthetic.main.nav_header.*
import javax.inject.Inject

class NotesFragment :
    DaggerFragment(),
    NotesRecyclerAdapter.Interaction,
    NavMenuAdapter.Interaction {

    lateinit var viewModel: NotesViewModel
    private lateinit var navMenuAdapter: NavMenuAdapter
    private lateinit var notesAdapter: NotesRecyclerAdapter
    private lateinit var listID: String
    private lateinit var uiController: UIController

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        uiController = (context as UIController)
        Log.d("!@#Notes Fragment", "onAttach: ")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("!@#Notes Fragment", "onCreate: ")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("!@#Notes Fragment", "onCreateView: ")
        viewModel =
            ViewModelProvider(viewModelStore, providerFactory).get(NotesViewModel::class.java)
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("!@#Notes Fragment", "onViewCreated: ")
        setupViews()
        setupNavDrawer()
        // FAB
        notesFragmentFab.setOnClickListener {
            uiController.displayInputDialog(
                "Новая заметка",
                object : UIController.Companion.InputDialogCallback {
                    override fun onInputComplete(text: String) {
                        viewModel.setStateEvent(
                            NoteListStateEvent.InsertNewNoteEvent(
                                title = text,
                                listId = listID
                            )
                        )
                    }

                })
        }

        subscribeObservers()
        setupOnBackPressDispatcher()

    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            if (viewState != null) {
                viewState.noteLists?.let {
                    navMenuAdapter.submitList(it)
                }

                viewState.newNote?.let {
                    navToNoteDetail(it)
                }

                viewState.notes?.let {
                    notesAdapter.submitList(it)
                }

                viewState.selectedNoteList?.let {
                    listID = it.id
                }
            }
        }
    }

    private fun setupViews() {
        // Toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
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
        addNoteListBtn.setOnClickListener { showToast("Click add list") }

        // recycler view
        navMenuAdapter = NavMenuAdapter(interaction = this)
        navRecyclerMenu.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = navMenuAdapter
        }
        navRecyclerMenu.addItemDecoration(VerticalItemDecoration(30))


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
    }

    // Note clicked
    override fun onItemSelected(position: Int, item: Note) {
        navToNoteDetail(item)
    }

    // List clicked
    override fun onItemSelected(position: Int, item: NoteList) {
        drawer.closeDrawer(GravityCompat.START)
        viewModel.setStateEvent(NoteListStateEvent.SelectNoteListEvent(item))
        viewModel.setSelectedList(item)
    }

    // List long click
    override fun onLongClick(position: Int, item: NoteList) {
        navToNoteListDetail(item)
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun navToNoteDetail(selectedNote: Note) {
        val bundle = bundleOf("NOTE_DETAIL_SELECTED_NOTE_BUNDLE_KEY" to selectedNote)

        findNavController().navigate(
            R.id.action_notesFragment_to_noteDetailFragment,
            bundle
        )
        drawer.closeDrawer(GravityCompat.START)
        viewModel.setNote(null)
    }

    private fun navToNoteListDetail(selectedNoteList: NoteList) {
        val bundle = bundleOf("NOTE_LIST_DETAIL_SELECTED_NOTE_BUNDLE_KEY" to selectedNoteList)

        findNavController().navigate(
            R.id.action_notesFragment_to_noteListDetailFragment,
            bundle
        )
        drawer.closeDrawer(GravityCompat.START)
    }


    override fun onResume() {
        super.onResume()
        viewModel.reloadListItems()
    }

}