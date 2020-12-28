package com.andrewbutch.noteeverything.framework.ui.notes

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.interactors.common.DeleteNoteList
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NOTE_FILTER_DATE_CREATED
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NOTE_FILTER_TITLE
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NOTE_ORDER_ASC
import com.andrewbutch.noteeverything.framework.datasource.cache.database.NOTE_ORDER_DESC
import com.andrewbutch.noteeverything.framework.session.SessionManager
import com.andrewbutch.noteeverything.framework.ui.BaseFragment
import com.andrewbutch.noteeverything.framework.ui.main.UIController.Companion.InputDialogCallback
import com.andrewbutch.noteeverything.framework.ui.notedetail.NoteDetailFragment.Companion.NOTE_DETAIL_BUNDLE_KEY
import com.andrewbutch.noteeverything.framework.ui.notelistdetail.NoteListDetailFragment.Companion.NOTE_LIST_DETAIL_BUNDLE_KEY
import com.andrewbutch.noteeverything.framework.ui.notes.drawer.NavMenuAdapter
import com.andrewbutch.noteeverything.framework.ui.notes.state.NoteListStateEvent
import com.andrewbutch.noteeverything.framework.ui.utils.ItemTouchHelperCallback
import com.andrewbutch.noteeverything.framework.ui.utils.VerticalItemDecoration
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_notes.*
import kotlinx.android.synthetic.main.fragment_notes_content.*
import kotlinx.android.synthetic.main.nav_header.*
import timber.log.Timber
import javax.inject.Inject

class NotesFragment :
    BaseFragment(R.layout.fragment_notes),
    NotesRecyclerAdapter.Interaction,
    NavMenuAdapter.Interaction {

    lateinit var viewModel: NotesViewModel
    private lateinit var navMenuAdapter: NavMenuAdapter
    private lateinit var notesAdapter: NotesRecyclerAdapter

    private var imgComplete: Drawable? = null
    private var imgUncomplete: Drawable? = null

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var sessionManager: SessionManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgComplete = ResourcesCompat.getDrawable(resources, R.drawable.ic_completed, null)
        imgUncomplete = ResourcesCompat.getDrawable(resources, R.drawable.ic_uncompleted, null)

        requireActivity().run {
            viewModel = ViewModelProvider(this, providerFactory).get(NotesViewModel::class.java)
        }
        viewModel.setUser(sessionManager.authUser.value!!)

        setupViews()
        setupNavDrawer()
        // FAB
        notesFragmentFab.setOnClickListener {
            showInputDialog("Новая заметка", InputDialogType.NOTE)
        }

        subscribeObservers()


        viewModel.setStateEvent(NoteListStateEvent.GetAllNoteListsEvent(sessionManager.authUser.value!!))


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
                                        listId = selectedNoteList.id,
                                        user = sessionManager.authUser.value!!
                                    )
                                )

                            }
                        }
                        InputDialogType.LIST -> {
                            viewModel.setStateEvent(
                                NoteListStateEvent.InsertNewNoteListEvent(
                                    title = text,
                                    user = sessionManager.authUser.value!!
                                )
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
                        getFromPreferences()
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
                        setTitle("")
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

        // Recycler
        notesAdapter = NotesRecyclerAdapter(
            interaction = this,
            imgChecked = imgComplete,
            imgUnchecked = imgUncomplete
        )
        recycler.apply {
            adapter = notesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        recycler.addItemDecoration(VerticalItemDecoration(30))

        // Item touch helper
        val touchHelperCallback = ItemTouchHelperCallback(notesAdapter)
        val itemTouchHelper = ItemTouchHelper(touchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recycler)
    }

    override fun onBackPressed() {
        requireActivity().finish()
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

    private fun getFromPreferences() {
        val selectedNoteList = preferences.getString(SELECTED_NOTE, null)
        selectedNoteList?.let {
            viewModel.setSelectedList(selectedNoteList)
        }
    }

    private fun getSyncPreference(): Boolean = preferences.getBoolean(SYNC, true)

    private fun setPreferences() {
        val editor = preferences.edit()
        editor.putString(SELECTED_NOTE, viewModel.getSelectedNoteList()?.id)
        editor.apply()
    }

    private fun setSyncPreferences(enableSync: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(SYNC, enableSync)
        editor.apply()
    }

    // Note clicked
    override fun onItemSelected(item: Note) {
        navToNoteDetail(item)
    }

    override fun onItemCheck(item: Note) {
        uiController.showToast("Toggle item ${item.title}")
        viewModel.beginPendingNoteUpdate(item)
    }

    override fun onItemDismiss(item: Note) {
        viewModel.beginPendingNoteDelete(item, sessionManager.authUser.value!!)
    }

    // List clicked
    override fun onItemSelected(position: Int, item: NoteList) {
        drawer.closeDrawer(GravityCompat.START)
        viewModel.setStateEvent(
            NoteListStateEvent.SelectNoteListEvent(
                noteList = item,
                user = sessionManager.authUser.value!!
            )
        )
    }

    // List long click
    override fun onLongClick(position: Int, item: NoteList) {
        navToNoteListDetail(item)
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
        viewModel.reloadNoteLists(sessionManager.authUser.value!!)
        viewModel.reloadNotes(sessionManager.authUser.value!!)
    }

    override fun onPause() {
        super.onPause()
        setPreferences()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_notes, menu)
        val syncEnable = getSyncPreference()
        menu.findItem(R.id.synch).isChecked = syncEnable
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                viewModel.setStateEvent(
                    NoteListStateEvent.DeleteAllNoteListsEvent(sessionManager.authUser.value!!)
                )
                setSyncPreferences(true)
                sessionManager.logout()
                true
            }
            R.id.info -> {
                uiController.showToast(sessionManager.authUser.value!!.id)
                true
            }
            R.id.insert -> {
                viewModel.insertTestData()
                true
            }
            R.id.synch -> {
                val enableSync = !item.isChecked
                item.isChecked = enableSync
                setSyncPreferences(enableSync)
                true
            }
            R.id.filter -> {
                showFilterDialog()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun showFilterDialog() {
        requireActivity().also {
            val dialog = MaterialDialog(it)
                .noAutoDismiss()
                .customView(R.layout.layout_filter_options, noVerticalPadding = true)

            val view = dialog.getCustomView()
            view.setPadding(0, 0,0,0,)

            val filterOption = viewModel.getFilter()
            val orderOption = viewModel.getOrder()

            view.findViewById<RadioGroup>(R.id.filterTypeGroup).apply {
                when (filterOption) {
                    NOTE_FILTER_DATE_CREATED -> check(R.id.filter_by_created_date)
                    NOTE_FILTER_TITLE -> check(R.id.filter_by_title)
                }
            }

            view.findViewById<RadioGroup>(R.id.filterOrderGroup).apply {
                when (orderOption) {
                    NOTE_ORDER_DESC -> check(R.id.filter_desc)
                    NOTE_ORDER_ASC -> check(R.id.filter_asc)
                }
            }

            view.findViewById<TextView>(R.id.filter_apply_btn).setOnClickListener {
                val newFilterOption =
                    when (view.findViewById<RadioGroup>(R.id.filterTypeGroup).checkedRadioButtonId) {
                        R.id.filter_by_title -> NOTE_FILTER_TITLE
                        R.id.filter_by_created_date -> NOTE_FILTER_DATE_CREATED
                        else -> NOTE_FILTER_DATE_CREATED

                    }

                val newOrderOption =
                    when (view.findViewById<RadioGroup>(R.id.filterOrderGroup).checkedRadioButtonId) {
                        R.id.filter_desc -> NOTE_ORDER_DESC
                        R.id.filter_asc -> NOTE_ORDER_ASC
                        else -> NOTE_ORDER_DESC

                    }
                // TODO save filter options in shared prefs
                viewModel.setNoteFilter(newFilterOption)
                viewModel.setNoteOrder(newOrderOption)
                viewModel.reloadNotes(sessionManager.authUser.value!!)
                dialog.dismiss()
            }

            view.findViewById<MaterialButton>(R.id.filter_cancel_btn).setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }


    enum class InputDialogType {
        NOTE, LIST
    }

    companion object {
        const val SELECTED_NOTE = "com.andrewbutch.noteeverything.framework.ui.notes.SELECTED_NOTE"
        const val SYNC = "com.andrewbutch.noteeverything.framework.ui.notes.SYNC"
    }

}