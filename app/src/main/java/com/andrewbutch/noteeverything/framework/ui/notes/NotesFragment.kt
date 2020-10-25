package com.andrewbutch.noteeverything.framework.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.framework.datasource.NoteDataFactory
import com.andrewbutch.noteeverything.framework.ui.notes.drawer.NavMenuAdapter
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_notes.*
import kotlinx.android.synthetic.main.layout_fragment_notes_content.*
import kotlinx.android.synthetic.main.nav_header.*
import javax.inject.Inject

class NotesFragment :
    DaggerFragment(),
    NotesRecyclerAdapter.Interaction,
    NavMenuAdapter.Interaction{

    lateinit var viewModel: NotesViewModel
//
//    @Inject
//    lateinit var noteFactory: NoteFactory
//
//    @Inject
//    lateinit var noteListFactory: NoteListFactory
//
    @Inject
    lateinit var noteDataFactory: NoteDataFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        setupNavDrawer()
        notesFragmentFab.setOnClickListener { _ ->
            showToast("Click fab")
        }
    }

    private fun setupViews() {
        // Toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
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
        val navMenuAdapter = NavMenuAdapter(interaction = this)
        navRecyclerMenu.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = navMenuAdapter
        }
//        navMenuAdapter.submitList(noteListFactory.createMultipleNoteList(20))
        navMenuAdapter.submitList(noteDataFactory.produceListOfNoteList())

        NavigationUI.setupActionBarWithNavController(
            requireActivity() as AppCompatActivity,
            findNavController(),
            drawer
        )
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NotesViewModel::class.java)
        // TODO: Use the ViewModel
        val notesAdapter = NotesRecyclerAdapter(interaction = this)
        recycler.apply {
            adapter = notesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

//        notesAdapter.submitList(noteFactory.createNoteList(20, "23"))
        notesAdapter.submitList(noteDataFactory.produceListOfNotes())
    }

    override fun onItemSelected(position: Int, item: Note) {
        showToast("Clicked $position, title: ${item.title}")
        navToNoteDetail(item)
    }

    override fun onItemSelected(position: Int, item: NoteList) {
        showToast("Clicked $position, title: ${item.title}")
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
    }

    private fun navToNoteListDetail(selectedNoteList: NoteList) {
        val bundle = bundleOf("NOTE_LIST_DETAIL_SELECTED_NOTE_BUNDLE_KEY" to selectedNoteList)
        findNavController().navigate(
            R.id.action_notesFragment_to_noteListDetailFragment,
            bundle
        )
    }
}