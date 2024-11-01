package com.larrykin.chemistpos.home.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface SalesHistoryDao {
    //insert income
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(salesHistory: SalesHistory) : Long

    //get all income
    @Query("SELECT * FROM sales_history")
    fun getAllSalesHistory() : Flow<List<SalesHistory>>

    //get sales history by date
    @Query("SELECT * FROM sales_history WHERE date = :date")
    fun getSalesHistoryByDate(date: Date): Flow<List<SalesHistory>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAll(salesHistory: List<SalesHistory>)

}