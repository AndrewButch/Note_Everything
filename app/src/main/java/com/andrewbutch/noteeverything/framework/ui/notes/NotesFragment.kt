package com.andrewbutch.noteeverything.framework.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteFactory
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.notes_fragment.*
import javax.inject.Inject

class NotesFragment :
    DaggerFragment(),
    NotesRecyclerAdapter.Interaction {

    lateinit var viewModel: NotesViewModel

    @Inject
    lateinit var noteFactory: NoteFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.notes_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        notesFragmentFab.setOnClickListener { _ ->
            showToast("Click fab")
        }
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

        notesAdapter.submitList(noteFactory.createNoteList(20, "23"))
    }

    override fun onItemSelected(position: Int, item: Note) {
        showToast("Clicked $position, title: ${item.title}")
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}