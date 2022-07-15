package com.s1aks.locchecker.domain

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.time.LocalDateTime

class DateTimeDBConverter {
    @SuppressLint("NewApi")
    @TypeConverter
    fun toDate(dateString: String?): LocalDateTime? =
        dateString?.let { LocalDateTime.parse(dateString) }

    @TypeConverter
    fun toDateString(date: LocalDateTime?): String? = date?.toString()
}