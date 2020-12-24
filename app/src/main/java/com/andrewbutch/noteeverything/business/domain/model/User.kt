package com.andrewbutch.noteeverything.business.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String,
    val displayName: String?,
    val email: String?
): Parcelable