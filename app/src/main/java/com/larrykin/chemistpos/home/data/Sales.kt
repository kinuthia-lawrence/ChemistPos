package com.larrykin.chemistpos.home.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "sales")
data class Sales(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "items") val items: List<SaleItem>,
    @ColumnInfo(name = "total_price") val totalPrice: Double,
    @ColumnInfo(name = "expected_amount") val expectedAmount: Double,
    @ColumnInfo(name = "cash") val cash: Double,
    @ColumnInfo(name = "mpesa") val mpesa: Double,
    @ColumnInfo(name = "discount") val discount: Double,
    @ColumnInfo(name = "credit") val credit: Double,
    @ColumnInfo(name = "seller") val seller: String,
    @ColumnInfo(name = "date") val date: Date,
)

data class SaleItem(
    val productId: Int,
    val quantity: Int,
)