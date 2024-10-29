package com.larrykin.chemistpos.home.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "sales_history")
data class SalesHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "cash") val cash: Double,
    @ColumnInfo(name = "mpesa") val mpesa: Double,
    @ColumnInfo(name = "discount") val discount: Double,
    @ColumnInfo(name = "credit") val credit: Double,
    @ColumnInfo(name = "services_cash") val servicesCash: Double,
    @ColumnInfo(name = "services_mpesa") val servicesMpesa: Double,
    @ColumnInfo(name = "date") val date: Date
)
