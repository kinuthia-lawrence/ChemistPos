package com.larrykin.chemistpos.core.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.larrykin.chemistpos.home.data.SaleItem
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

    //    convert the number object to a long and vice versa
    @TypeConverter
    fun fromNumber(value: Long?): Number? {
        return value
    }

    @TypeConverter
    fun numberToLong(number: Number?): Long? {
        return number?.toLong()
    }

    //    convert the list of strings to a string and vice versa
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Gson().toJson(list)
    }

    //    convert the list of SaleItem to a string and vice versa
    @TypeConverter
    fun fromSaleItemList(value: List<SaleItem>): String {
        val gson = Gson()
        val type = object : TypeToken<List<SaleItem>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toSaleItemList(value: String): List<SaleItem> {
        val gson = Gson()
        val type = object : TypeToken<List<SaleItem>>() {}.type
        return gson.fromJson(value, type)
    }

}