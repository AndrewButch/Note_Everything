package com.andrewbutch.noteeverything.framework.ui.notes

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

class NotesFragment : BaseFragment(R.layout.fragment_notes) {

    lateinit var viewModel: NotesViewModel
    private lateinit var navMenuAdapter: NavMenuAdapter
    private lateinit var notesAdapter: NotesRecyclerAdapter

    private var imgComplete: Drawable? = null
    private var imgUncomplete: Drawable? = null

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sessionManager: SessionManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgComplete = ResourcesCompat.getDrawable(resources, R.drawable.ic_completed, null)
        imgUncomplete = ResourcesCompat.getDrawable(resources, R.drawable.ic_uncompleted, null)

        requireActivity().run {
            viewModel = ViewModelProvider(this, providerFactory).get(NotesViewModel::class.java)
        }

        setupViews()
        viewModel.apply {
            setUser(sessionManager.authUser.value!!)
            setStateEvent(NoteListStateEvent.GetAllNoteListsEvent(sessionManager.authUser.value!!))
        }
        subscribeObservers()
    }


    private fun setupViews() {
        // Toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        setupNotesRecycler()
        setupNavDrawer()
        setupFab()
    }

    private fun setupNotesRecycler() {
        hideNotesContainer()
        notesAdapter = NotesRecyclerAdapter(
            interaction = NotesAdapterInteractor(),
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
        ItemTouchHelper(touchHelperCallback).apply {
            attachToRecyclerView(recycler)
        }
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
        navMenuAdapter = NavMenuAdapter(interaction = NavDrawerAdapterInteractor())
        navRecyclerMenu.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = navMenuAdapter
        }
        navRecyclerMenu.addItemDecoration(VerticalItemDecoration(20))
    }

    private fun setupFab() {
        notesFragmentFab.setOnClickListener {
            showInputDialog("Новая заметка", InputDialogType.NOTE)
        }
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
                        viewModel.initSelectedNoteList()
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
                        viewModel.setSelectedNoteList(selectedList = null)
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

    // Hide note recycler view and fab
    private fun hideNotesContainer() {
        notes_container.visibility = View.GONE
        notesFragmentFab.visibility = View.GONE
    }

    // Show note recycler view and fab
    private fun showNotesContainer() {
        notes_container.visibility = View.VISIBLE
        notesFragmentFab.visibility = View.VISIBLE
    }

    private fun setTitle(title: String) {
        toolbar.title = title
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

    private fun showFilterDialog() {
        requireActivity().also {
            val dialog = MaterialDialog(it)
                .noAutoDismiss()
                .customView(R.layout.layout_filter_options, noVerticalPadding = true)

            val view = dialog.getCustomView()
            val filterOption = viewModel.getFilter()
            val orderOption = viewModel.getOrder()

            setupFilterTypeOptions(view, filterOption)
            setupFilterOrderOptions(view, orderOption)
            setupFilterApplyBtn(view, dialog)
            setupFilterCancelBtn(view, dialog)

            dialog.show()
        }
    }

    private fun setupFilterTypeOptions(view: View, filterOption: String) {
        view.findViewById<RadioGroup>(R.id.filterTypeGroup).apply {
            when (filterOption) {
                NOTE_FILTER_DATE_CREATED -> check(R.id.filter_by_created_date)
                NOTE_FILTER_TITLE -> check(R.id.filter_by_title)
            }
        }
    }

    private fun setupFilterOrderOptions(view: View, orderOption: String) {
        view.findViewById<RadioGroup>(R.id.filterOrderGroup).apply {
            when (orderOption) {
                NOTE_ORDER_DESC -> check(R.id.filter_desc)
                NOTE_ORDER_ASC -> check(R.id.filter_asc)
            }
        }
    }

    private fun setupFilterApplyBtn(view: View, dialog: MaterialDialog) {
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
            viewModel.apply {
                setNoteFilter(newFilterOption)
                setNoteOrder(newOrderOption)
                reloadNotes(sessionManager.authUser.value!!)
            }
            dialog.dismiss()
        }
    }

    private fun setupFilterCancelBtn(view: View, dialog: MaterialDialog) {
        view.findViewById<MaterialButton>(R.id.filter_cancel_btn).setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.reloadNoteLists(sessionManager.authUser.value!!)
        viewModel.reloadNotes(sessionManager.authUser.value!!)
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveSharedPreference()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_notes, menu)
        val syncEnable = viewModel.getSyncOption()
        menu.findItem(R.id.synch).isChecked = syncEnable
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                viewModel.setStateEvent(
                    NoteListStateEvent.DeleteAllNoteListsEvent(sessionManager.authUser.value!!)
                )
                sessionManager.logout()
                true
            }
            R.id.synch -> {
                val enableSync = !item.isChecked
                item.isChecked = enableSync
                viewModel.setSyncOption(enableSync)
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

    override fun onBackPressed() {
        requireActivity().finish()
    }

    enum class InputDialogType {
        NOTE, LIST
    }

    inner class NotesAdapterInteractor : NotesRecyclerAdapter.Interaction {
        override fun onItemSelected(item: Note) {
            this@NotesFragment.navToNoteDetail(item)
        }

        override fun onItemCheck(item: Note) {
            this@NotesFragment.viewModel.beginPendingNoteUpdate(item)
        }

        override fun onItemDismiss(item: Note) {
            this@NotesFragment.viewModel.beginPendingNoteDelete(
                item,
                sessionManager.authUser.value!!
            )
        }

    }

    inner class NavDrawerAdapterInteractor : NavMenuAdapter.Interaction {
        override fun onItemSelected(position: Int, item: NoteList) {
            this@NotesFragment.drawer.closeDrawer(GravityCompat.START)
            viewModel.setStateEvent(
                NoteListStateEvent.SelectNoteListEvent(
                    noteList = item,
                    user = sessionManager.authUser.value!!
                )
            )
        }

        override fun onLongClick(position: Int, item: NoteList) {
            this@NotesFragment.navToNoteListDetail(item)
        }

    }
}