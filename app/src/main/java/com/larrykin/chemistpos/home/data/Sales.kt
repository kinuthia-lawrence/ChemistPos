package com.larrykin.chemistpos.home.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

data class SaleItem(
    val productId: Int,
    val quantity: Int,
){
    // No-argument constructor for Firestore
    constructor() : this(
        productId = 0,
        quantity = 0
    )
}

@Entity(tableName = "sales")
data class Sales(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "items") val items: List<SaleItem> = emptyList(),
    @ColumnInfo(name = "total_price") val totalPrice: Double = 0.0,
    @ColumnInfo(name = "expected_amount") val expectedAmount: Double = 0.0,
    @ColumnInfo(name = "cash") val cash: Double = 0.0,
    @ColumnInfo(name = "mpesa") val mpesa: Double = 0.0,
    @ColumnInfo(name = "discount") val discount: Double = 0.0,
    @ColumnInfo(name = "credit") val credit: Double = 0.0,
    @ColumnInfo(name = "seller") val seller: String = "",
    @ColumnInfo(name = "date") val date: Date = Date(),
    @ColumnInfo(name = "timestamp") var timestamp: Long = System.currentTimeMillis()
) {
    // No-argument constructor for Firestore
    constructor() : this(
        id = 0,
        items = emptyList(),
        totalPrice = 0.0,
        expectedAmount = 0.0,
        cash = 0.0,
        mpesa = 0.0,
        discount = 0.0,
        credit = 0.0,
        seller = "",
        date = Date(),
        timestamp = System.currentTimeMillis()
    )
}

