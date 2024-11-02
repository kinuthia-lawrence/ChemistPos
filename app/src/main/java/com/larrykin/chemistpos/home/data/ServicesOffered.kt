package com.larrykin.chemistpos.home.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "services_offered")
data class ServicesOffered(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "servitor") val servitor: String = "",
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "cash") val cash: Double? = 0.0,
    @ColumnInfo(name = "mpesa") val mpesa: Double? = 0.0,
    @ColumnInfo(name = "total_price") val totalPrice: Double = 0.0,
    @ColumnInfo(name = "expected_amount") val expectedAmount: Double? = 0.0,
    @ColumnInfo(name = "created_at") val createdAt: Date = Date(),
    @ColumnInfo(name = "updated_at") val updatedAt: Date? = null,
    @ColumnInfo(name = "timestamp") var timestamp: Long = System.currentTimeMillis()
) {
    // No-argument constructor for Firestore
    constructor() : this(
        id = 0,
        name = "",
        servitor = "",
        description = "",
        cash = 0.0,
        mpesa = 0.0,
        totalPrice = 0.0,
        expectedAmount = 0.0,
        createdAt = Date(),
        updatedAt = null,
        timestamp = System.currentTimeMillis()
    )
}