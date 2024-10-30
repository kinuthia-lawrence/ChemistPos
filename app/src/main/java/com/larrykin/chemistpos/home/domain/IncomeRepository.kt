package com.larrykin.chemistpos.home.domain

import com.larrykin.chemistpos.home.data.Income
import kotlinx.coroutines.flow.Flow

interface IncomeRepository {
    suspend fun insertIncome(income: Income): Long?
    suspend fun getAllIncome(): Flow<List<Income>>
    suspend fun getFirstIncome(): Income?
    suspend fun updateIncome(income: Income): Int
}