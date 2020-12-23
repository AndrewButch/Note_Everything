package com.andrewbutch.noteeverything.framework.datasource.network.implementation

import com.andrewbutch.noteeverything.business.domain.model.Note
import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.NoteFirestoreService
import com.andrewbutch.noteeverything.framework.datasource.network.mapper.NoteNetworkMapper
import com.andrewbutch.noteeverything.framework.datasource.network.model.NoteNetworkEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Firestore doc refs:
 * 1. add:  https://firebase.google.com/docs/firestore/manage-data/add-data
 * 2. delete: https://firebase.google.com/docs/firestore/manage-data/delete-data
 * 3. update: https://firebase.google.com/docs/firestore/manage-data/add-data#update-data
 * 4. query: https://firebase.google.com/docs/firestore/query-data/queries
 */
class NoteFirestoreServiceImpl
constructor(
    private val store: FirebaseFirestore,
    private val mapper: NoteNetworkMapper
) : NoteFirestoreService {
    override suspend fun insertOrUpdateNote(note: Note, user: User) {
        val entity = mapper.mapToEntity(note)
        store
            .collection(NOTES_COLLECTION)
            .document(user.id)
            .collection(NOTES_COLLECTION)
            .document(entity.id)
            .set(entity)
            .await()
    }

    override suspend fun deleteNote(note: Note, user: User) {
        store
            .collection(NOTES_COLLECTION)
            .document(user.id)
            .collection(NOTES_COLLECTION)
            .document(note.id)
            .delete()
    }

    override suspend fun deleteNotesByOwnerListId(ownerListId: String, user: User) {
        val notes = store
            .collection(NOTES_COLLECTION)
            .document(user.id)
            .collection(NOTES_COLLECTION)
            .whereEqualTo("listId", ownerListId)
            .get()
            .await()
            .toObjects(NoteNetworkEntity::class.java)

        for (note in notes) {
            store
                .collection(NOTES_COLLECTION)
                .document(user.id)
                .collection(NOTES_COLLECTION)
                .document(note.id)
                .delete()
                .await()
        }
    }

    override suspend fun searchNote(note: Note, user: User): Note? {
        return store
            .collection(NOTES_COLLECTION)
            .document(user.id)
            .collection(NOTES_COLLECTION)
            .document(note.id)
            .get()
            .await()
            .toObject(NoteNetworkEntity::class.java)?.let {
                mapper.mapFromEntity(it)
            }
    }

    override suspend fun getNotesByOwnerListId(ownerListId: String, user: User): List<Note> {
        return mapper.mapFromEntityList(
            store
                .collection(NOTES_COLLECTION)
                .document(user.id)
                .collection(NOTES_COLLECTION)
                .whereEqualTo("listId", ownerListId)
                .get()
                .await()
                .toObjects(NoteNetworkEntity::class.java)
        )
    }

    companion object {
        const val NOTES_COLLECTION = "notes"
    }
}