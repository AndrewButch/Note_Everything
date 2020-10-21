package com.andrewbutch.noteeverything.framework.datasource.network.implementation

import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.NoteFirestoreService
import com.andrewbutch.noteeverything.framework.datasource.network.mapper.NoteNetworkMapper
import com.andrewbutch.noteeverything.framework.datasource.network.model.NoteNetworkEntity
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
class NoteFirestoreServiceImpl
@Inject
constructor(
    private val store: FirebaseFirestore,
    private val mapper: NoteNetworkMapper
) : NoteFirestoreService {
    override suspend fun insertOrUpdateNote(note: Note) {
        val entity = mapper.mapToEntity(note)
        store
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(entity.id)
            .set(entity)
            .await()
    }

    override suspend fun deleteNote(note: Note) {
        store
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(note.id)
            .delete()
    }

    override suspend fun deleteNotesByOwnerListId(ownerListId: String) {
        val notes = store
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .whereEqualTo("listId", ownerListId)
            .get()
            .await()
            .toObjects(NoteNetworkEntity::class.java)

        for (note in notes) {
            store
                .collection(NOTES_COLLECTION)
                .document(USER_ID)
                .collection(NOTES_COLLECTION)
                .document(note.id)
                .delete()
                .await()
        }
    }

    override suspend fun searchNote(note: Note): Note? {
        return store
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(note.id)
            .get()
            .await()
            .toObject(NoteNetworkEntity::class.java)?.let {
                mapper.mapFromEntity(it)
            }
    }

    override suspend fun getNotesByOwnerListId(ownerListId: String): List<Note> {
        return mapper.mapFromEntityList(
            store
                .collection(NOTES_COLLECTION)
                .document(USER_ID)
                .collection(NOTES_COLLECTION)
                .whereEqualTo("listId", ownerListId)
                .get()
                .await()
                .toObjects(NoteNetworkEntity::class.java)
        )
    }

    companion object {
        const val NOTES_COLLECTION = "notes"
        const val USER_ID = "jLfWxedaCBdpxvcdfVpdzQIfzDw2" // hardcoded for single user
    }
}