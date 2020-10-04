package com.andrewbutch.noteeverything.di

import com.andrewbutch.noteeverything.business.data.NoteDataFactory
import com.andrewbutch.noteeverything.business.data.cache.FakeNoteCacheDataSourceImpl
import com.andrewbutch.noteeverything.business.data.cache.FakeNoteListCacheDataSourceImpl
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteCacheDataSource
import com.andrewbutch.noteeverything.business.data.cache.abstraction.NoteListCacheDataSource
import com.andrewbutch.noteeverything.business.data.network.FakeNoteListNetworkDataSourceImpl
import com.andrewbutch.noteeverything.business.data.network.FakeNoteNetworkDataSourceImpl
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteListNetworkDataSource
import com.andrewbutch.noteeverything.business.data.network.abstraction.NoteNetworkDataSource
import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.NoteFactory
import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.business.domain.model.NoteListFactory
import com.andrewbutch.noteeverything.business.domain.util.DateUtil
import java.text.SimpleDateFormat
import java.util.*

class DependencyContainer {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy hh:mm:ss", Locale.ENGLISH)
    private val dateUtil = DateUtil(dateFormat)
    lateinit var noteNetworkDataSource: NoteNetworkDataSource
    lateinit var noteCacheDataSource: NoteCacheDataSource
    lateinit var noteListCacheDataSource: NoteListCacheDataSource
    lateinit var noteListNetworkDataSource: NoteListNetworkDataSource
    lateinit var noteFactory: NoteFactory
    lateinit var noteListFactory: NoteListFactory
    lateinit var noteDataFactory: NoteDataFactory

    // data sets
    lateinit var noteListsData: HashMap<String, NoteList>
    lateinit var notesData: HashMap<String, Note>

    fun build() {
        this.javaClass.classLoader?.let { classLoader ->
            noteDataFactory = NoteDataFactory(classLoader)

            // fake note list data set
            noteListsData = noteDataFactory.produceHashMapOfNoteLists(
                noteDataFactory.produceListOfNoteLists()
            )

            // fake note data set
            notesData = noteDataFactory.produceHashMapOfNotes(
                noteDataFactory.produceListOfNotes()
            )
        }
        noteFactory = NoteFactory(dateUtil)
        noteListFactory = NoteListFactory(dateUtil)

        noteNetworkDataSource = FakeNoteNetworkDataSourceImpl(
            data = notesData
        )

        noteCacheDataSource = FakeNoteCacheDataSourceImpl(
            data = notesData,
            dateUtil = dateUtil
        )

        noteListNetworkDataSource = FakeNoteListNetworkDataSourceImpl(
            data = noteListsData,
        )

        noteListCacheDataSource = FakeNoteListCacheDataSourceImpl(
            data = noteListsData,
            dateUtil = dateUtil
        )

    }
}