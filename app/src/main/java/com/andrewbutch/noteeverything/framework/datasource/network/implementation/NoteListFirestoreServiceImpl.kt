package com.andrewbutch.noteeverything.framework.datasource.network.implementation

import com.andrewbutch.noteeverything.business.domain.model.NoteList
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.NoteListFirestoreService
import com.andrewbutch.noteeverything.framework.datasource.network.mapper.NoteListNetworkMapper
import com.andrewbutch.noteeverything.framework.datasource.network.model.NoteListNetworkEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore doc refs:
 * 1. add:  https://firebase.google.com/docs/firestore/manage-data/add-data
 * 2. delete: https://firebase.google.com/docs/firestore/manage-data/delete-data
 * 3. update: https://firebase.google.com/docs/firestore/manage-data/add-data#update-data
 * 4. query: https://firebase.google.com/docs/firestore/query-data/queries
 */
@Singleton
class NoteListFirestoreServiceImpl
@Inject
constructor(
    private val store: FirebaseFirestore,
    private val mapper: NoteListNetworkMapper,
) : NoteListFirestoreService {
    override suspend fun insertOrUpdateNoteList(noteList: NoteList) {
        val entity = mapper.mapToEntity(noteList)
        store
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(entity.id)
            .set(entity)
            .await()
    }

    override suspend fun deleteNoteList(id: String) {
        store
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(id)
            .delete()
            .await()
    }

    override suspend fun deleteAllNotesLists() {
        store
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .delete()
            .await()
    }

    override suspend fun searchNoteList(noteList: NoteList): NoteList? {
        return store
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(noteList.id)
            .get()
            .await()
            .toObject(NoteListNetworkEntity::class.java)?.let {
                mapper.mapFromEntity(it)
            }
    }

    override suspend fun getAllNotes(): List<NoteList> {
        return mapper.mapFromEntityList(
            store
                .collection(NOTES_COLLECTION)
                .document(USER_ID)
                .collection(NOTES_COLLECTION)
                .get()
                .await()
                .toObjects(NoteListNetworkEntity::class.java)
        )
    }

    companion object {
        const val NOTES_COLLECTION = "notes"
        const val USER_ID = "jLfWxedaCBdpxvcdfVpdzQIfzDw2" // hardcoded for single user
    }
}