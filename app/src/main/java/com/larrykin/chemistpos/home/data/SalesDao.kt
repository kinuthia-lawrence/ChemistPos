package com.larrykin.chemistpos.home.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface SalesDao {

    @Insert
    suspend fun insertSale(sales: Sales): Long?

    @Query("SELECT * FROM sales")
    fun getAllSales(): Flow<List<Sales>>

    @Query("SELECT * FROM sales WHERE date = :date")
    fun getSalesByDate(date: Date): Flow<List<Sales>>
}