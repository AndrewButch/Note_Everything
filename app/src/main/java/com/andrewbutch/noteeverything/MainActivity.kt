package com.andrewbutch.noteeverything

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.andrewbutch.noteeverything.business.domain.model.NoteFactory
import com.andrewbutch.noteeverything.business.domain.model.NoteListFactory
import com.andrewbutch.noteeverything.business.domain.util.DateUtil
import com.andrewbutch.noteeverything.framework.BaseApplication
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.NoteFirestoreService
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.NoteListFirestoreService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var dateUtil: DateUtil

    @Inject
    lateinit var firestoreService: NoteListFirestoreService

    @Inject
    lateinit var firestoreServiceNote: NoteFirestoreService

    @Inject
    lateinit var noteListFactory: NoteListFactory

    @Inject
    lateinit var noteFactory: NoteFactory

    lateinit var listId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as BaseApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        date.text = dateUtil.getCurrentTimestamp()

        insert_list_btn.setOnClickListener { insertTestList() }
        insert_note_btn.setOnClickListener { insertTestNote()}
        delete_list_btn.setOnClickListener { deleteTestList() }
        delete_note_btn.setOnClickListener { deleteTestNote() }

    }

    private fun insertTestList() {
        CoroutineScope(Dispatchers.IO).launch {
            val noteList = noteListFactory.createNoteList(title = "Hello world")
            listId = noteList.id
            firestoreService.insertOrUpdateNoteList(noteList)
        }
    }

    private fun insertTestNote() {
        listId?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val note = noteFactory.createNote(
                    title = "Hello world note",
                    listId = "e59b3dd6-4653-4392-b150-d6734e2a99d8"
                )
                firestoreServiceNote.insertOrUpdateNote(note)
            }
        }

    }

    private fun deleteTestList() {
        CoroutineScope(Dispatchers.IO).launch {
            firestoreService.deleteAllNotesLists()
        }
    }

    private fun deleteTestNote() {
        CoroutineScope(Dispatchers.IO).launch {
            firestoreServiceNote.deleteNotesByOwnerListId(listId)
        }
    }
}