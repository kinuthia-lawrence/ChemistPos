package com.larrykin.chemistpos.home.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "services_offered")
data class ServicesOffered(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "servitor") val servitor: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "cash") val cash: Double?,
    @ColumnInfo(name = "mpesa") val mpesa: Double?,
    @ColumnInfo(name = "total_price") val totalPrice: Double,
    @ColumnInfo(name = "expected_amount") val expectedAmount: Double?,
    @ColumnInfo(name = "created_at") val createdAt: Date,
    @ColumnInfo(name = "updated_at") val updatedAt: Date?,
    @ColumnInfo(name = "timestamp") var timestamp: Long = System.currentTimeMillis()
)