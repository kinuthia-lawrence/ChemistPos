package com.larrykin.chemistpos.home.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "sales_history")
data class SalesHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "cash") val cash: Double = 0.0,
    @ColumnInfo(name = "mpesa") val mpesa: Double = 0.0,
    @ColumnInfo(name = "discount") val discount: Double = 0.0,
    @ColumnInfo(name = "credit") val credit: Double = 0.0,
    @ColumnInfo(name = "services_cash") val servicesCash: Double = 0.0,
    @ColumnInfo(name = "services_mpesa") val servicesMpesa: Double = 0.0,
    @ColumnInfo(name = "date") val date: Date = Date(),
    @ColumnInfo(name = "timestamp") var timestamp: Long = System.currentTimeMillis()
) {
    // No-argument constructor for Firestore
    constructor() : this(
        id = 0,
        cash = 0.0,
        mpesa = 0.0,
        discount = 0.0,
        credit = 0.0,
        servicesCash = 0.0,
        servicesMpesa = 0.0,
        date = Date(),
        timestamp = System.currentTimeMillis()
    )
}
