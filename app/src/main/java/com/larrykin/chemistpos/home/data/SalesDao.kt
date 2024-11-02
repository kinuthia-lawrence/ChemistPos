package com.larrykin.chemistpos.home.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface SalesDao {

    @Insert
    suspend fun insertSale(sales: Sales): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sales: List<Sales>)

    @Query("SELECT * FROM sales")
    fun getAllSales(): Flow<List<Sales>>

    @Query("SELECT * FROM sales WHERE date = :date")
    fun getSalesByDate(date: Date): Flow<List<Sales>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAll(sales: List<Sales>)

}