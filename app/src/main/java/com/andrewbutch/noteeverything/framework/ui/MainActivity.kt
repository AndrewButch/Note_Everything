package com.andrewbutch.noteeverything.framework.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.model.NoteListFactory
import com.andrewbutch.noteeverything.framework.ui.notes.drawer.NavMenuAdapter
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_container.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : DaggerAppCompatActivity(), NavMenuAdapter.Interaction {

    @Inject
    lateinit var noteListFactory: NoteListFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
    }

    private fun setupViews() {
        // Toolbar
        setSupportActionBar(toolbar)
        setupNavDrawer()

    }

    private fun setupNavDrawer() {
        // Toggle listener
        val toggle = ActionBarDrawerToggle(
            this,
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
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = navMenuAdapter
        }
        navMenuAdapter.submitList(noteListFactory.createMultipleNoteList(20))

        NavigationUI.setupActionBarWithNavController(
            this,
            findNavController(R.id.navHostFragmentContainer),
            drawer
        )
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onItemSelected(position: Int, item: NoteList) {
        showToast("Clicked $position, title: ${item.title}")
        navToNoteDetail(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(findNavController(R.id.navHostFragmentContainer), drawer)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    private fun navToNoteDetail(selectedNoteList: NoteList) {
        val bundle = bundleOf("NOTE_LIST_DETAIL_SELECTED_NOTE_BUNDLE_KEY" to selectedNoteList)
        findNavController(R.id.navHostFragmentContainer).navigate(
            R.id.action_notesFragment_to_noteListDetailFragment,
            bundle
        )
    }



}