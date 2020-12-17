package com.andrewbutch.noteeverything.framework.datasource.network.implementation

import com.andrewbutch.noteeverything.business.domain.model.User
import com.andrewbutch.noteeverything.di.auth.AuthScope
import com.andrewbutch.noteeverything.framework.datasource.network.abstraction.AuthFirestoreService
import com.andrewbutch.noteeverything.framework.datasource.network.mapper.UserNetworkMapper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AuthScope
class AuthFireStoreServiceImpl
@Inject
constructor(
    private val firebaseAuth: FirebaseAuth,
    private val mapper: UserNetworkMapper
) : AuthFirestoreService {

    override suspend fun login(email: String, password: String): User? {
        var user: User? = null
        val firebaseUser = firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()
            .user
        if (firebaseUser != null) {
            user = mapper.mapFromNetwork(firebaseUser)
        }
        return user
    }

    override suspend fun register(email: String, password: String): User? {
        var user: User? = null
        val firebaseUser = firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .await()
            .user
        if (firebaseUser != null) {
            user = mapper.mapFromNetwork(firebaseUser)
        }
        return user
    }

    override suspend fun getCurrentUser(): User? {
        var user: User? = null
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            user = mapper.mapFromNetwork(firebaseUser)
        }
        return user
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }


}