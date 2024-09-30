package com.larrykin.chemistpos.core.converters

import androidx.room.TypeConverter
import java.util.Date

open class Converters {
    //   convert the date object to a long and vice versa
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromNumber(value: Long?): Number? {
        return value
    }

    @TypeConverter
    fun numberToLong(number: Number?): Long? {
        return number?.toLong()
    }
}