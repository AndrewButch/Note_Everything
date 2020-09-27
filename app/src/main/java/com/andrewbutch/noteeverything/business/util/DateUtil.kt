package com.andrewbutch.noteeverything.business.util

import android.util.Log
import com.google.firebase.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateUtil
@Inject
constructor(
    private val dateFormat: SimpleDateFormat     // Date format: "2019-07-23 HH:mm:ss"
) {
    fun removeTimeFromDateString(sd: String): String {
        return sd.substring(0, sd.indexOf(" "))
    }

    fun convertFirebaseTimestampToStringData(timestamp: Timestamp): String {
        return dateFormat.format(timestamp.toDate())
    }

    fun convertStringDateToFirebaseTimestamp(date: String): Timestamp {
        try {
            dateFormat.parse(date)?.let {
                return Timestamp(it)
            }
        } catch (e: ParseException) {
            Log.e("DateUtil", "convertStringDateToFirebaseTimestamp: ", e)
        }
        return Timestamp(0, 0)
    }

    fun getCurrentTimestamp(): String {
        return dateFormat.format(Date())
    }

}