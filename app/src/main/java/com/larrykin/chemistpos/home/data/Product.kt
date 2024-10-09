package com.larrykin.chemistpos.home.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "company") val company: String,
    @ColumnInfo(name = "formulation") val formulation: String,
    @ColumnInfo(name = "min_stock") val minStock: Int,
    @ColumnInfo(name = "min_measure") val minMeasure: Int,
    @ColumnInfo(name = "quantity_available") val quantityAvailable: Int,
    @ColumnInfo(name = "buying_price") val buyingPrice: Double,
    @ColumnInfo(name = "retail_selling_price") val retailSellingPrice: Double,
    @ColumnInfo(name = "wholesale_selling_price") val wholesaleSellingPrice: Double,
    @ColumnInfo(name = "supplier_name") val supplierName: String,
    @ColumnInfo(name = "date_added") val dateAdded: Date,
    @ColumnInfo(name="updated_at") val updatedAt: Date,
    @ColumnInfo(name ="added_by") val addedBy: String,
    @ColumnInfo(name = "expiry_date") val expiryDate: Date?,
    @ColumnInfo(name = "description") val description: String?
)
