package com.larrykin.chemistpos.home.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "Phone") val phone: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "medicines") val medicines: List<String>
)
