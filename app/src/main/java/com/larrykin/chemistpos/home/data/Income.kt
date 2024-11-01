package com.larrykin.chemistpos.home.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income")
data class Income (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "cash") val cash: Double,
    @ColumnInfo(name = "mpesa") val mpesa: Double,
    @ColumnInfo(name = "stock_worth") val stockWorth: Double,
    @ColumnInfo(name = "services_cash") val servicesCash: Double,
    @ColumnInfo(name = "services_mpesa") val servicesMpesa: Double,
    @ColumnInfo(name = "profit") val profit: Double,
    @ColumnInfo(name = "loss") val loss: Double,
    @ColumnInfo(name = "timestamp") var timestamp: Long = System.currentTimeMillis()
)