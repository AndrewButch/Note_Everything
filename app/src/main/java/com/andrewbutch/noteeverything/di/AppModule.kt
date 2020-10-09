package com.andrewbutch.noteeverything.di

import com.andrewbutch.noteeverything.business.domain.util.DateUtil
import dagger.Module
import dagger.Provides
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton

@Module
object AppModule {

    // https://developer.android.com/reference/java/text/SimpleDateFormat.html?hl=pt-br
    @JvmStatic
    @Singleton
    @Provides
    fun provideDateFormat(): SimpleDateFormat {
        val sdf = SimpleDateFormat("dd.MM.yyyy hh:mm:ss", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC+3") // match firestore
        return sdf
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideDateUtil(dateFormat: SimpleDateFormat): DateUtil {
        return DateUtil(
            dateFormat
        )
    }
}