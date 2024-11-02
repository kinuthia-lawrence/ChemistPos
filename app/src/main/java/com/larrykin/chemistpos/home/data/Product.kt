package com.larrykin.chemistpos.home.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "company") val company: String = "",
    @ColumnInfo(name = "formulation") val formulation: String = "",
    @ColumnInfo(name = "min_stock") val minStock: Int = 0,
    @ColumnInfo(name = "min_measure") val minMeasure: Int = 0,
    @ColumnInfo(name = "quantity_available") val quantityAvailable: Int = 0,
    @ColumnInfo(name = "buying_price") val buyingPrice: Double = 0.0,
    @ColumnInfo(name = "retail_selling_price") val retailSellingPrice: Double = 0.0,
    @ColumnInfo(name = "wholesale_selling_price") val wholesaleSellingPrice: Double = 0.0,
    @ColumnInfo(name = "supplier_name") val supplierName: String = "",
    @ColumnInfo(name = "date_added") val dateAdded: Date = Date(),
    @ColumnInfo(name = "updated_at") val updatedAt: Date? = null,
    @ColumnInfo(name = "added_by") val addedBy: String = "",
    @ColumnInfo(name = "expiry_date") val expiryDate: Date = Date(),
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "timestamp") var timestamp: Long = System.currentTimeMillis()
) {
    // No-argument constructor for Firestore
    constructor() : this(
        id = 0,
        name = "",
        company = "",
        formulation = "",
        minStock = 0,
        minMeasure = 0,
        quantityAvailable = 0,
        buyingPrice = 0.0,
        retailSellingPrice = 0.0,
        wholesaleSellingPrice = 0.0,
        supplierName = "",
        dateAdded = Date(),
        updatedAt = null,
        addedBy = "",
        expiryDate = Date(),
        description = null,
        timestamp = System.currentTimeMillis()
    )
}
 