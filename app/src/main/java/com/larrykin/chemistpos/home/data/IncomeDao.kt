package com.larrykin.chemistpos.home.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    //insert income
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(income: Income): Long

    //get all income
    @Query("SELECT * FROM income")
    fun getAllIncome(): Flow<List<Income>>

    @Query("SELECT * FROM income LIMIT 1")
    suspend fun getFirstIncome(): Income?

    //update income
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(income: Income): Int


}