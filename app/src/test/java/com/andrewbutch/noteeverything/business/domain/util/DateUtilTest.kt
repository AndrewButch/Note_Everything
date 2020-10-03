package com.andrewbutch.noteeverything.business.domain.util

import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*


class DateUtilTest {
    private val dateFormat: SimpleDateFormat =
        SimpleDateFormat("dd.MM.yyyy hh:mm:ss", Locale.ENGLISH)
    private val dateUtil = DateUtil(dateFormat)
    private val stringDate = "03.10.2020 10:36:26"
    private val secondsInDate = 1601710586L

    @Test
    fun `convert String date to Firebase timestamp`() {
        val correctTimestamp = Timestamp(secondsInDate, 0)
        assertEquals(correctTimestamp, dateUtil.convertStringDateToFirebaseTimestamp(stringDate))
    }

    @Test
    fun `convert Firebase timestamp to String date`() {
        assertEquals(
            stringDate,
            dateUtil.convertFirebaseTimestampToStringDate(Timestamp(secondsInDate, 0))
        )
    }

    @Test
    fun `remove time from date string`() {
        val correct = "03.10.2020"
        assertEquals(correct, dateUtil.removeTimeFromDateString(stringDate))
    }
}