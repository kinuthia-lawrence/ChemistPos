package com.larrykin.chemistpos.home.domain

import com.larrykin.chemistpos.home.data.SalesHistory
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface SalesHistoryRepository {
    suspend fun insertSalesHistory(salesHistory: SalesHistory): Long?
    suspend fun getAllSalesHistory(): Flow<List<SalesHistory>>
    suspend fun getSalesHistoryByDate(date: Date): Flow<List<SalesHistory>>
}