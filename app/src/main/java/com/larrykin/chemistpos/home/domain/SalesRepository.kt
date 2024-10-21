package com.larrykin.chemistpos.home.domain

import com.larrykin.chemistpos.home.data.Sales
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface SalesRepository {
    suspend fun insertSale(sales: Sales): Long?
    suspend fun getAllSales(): Flow<List<Sales>>
    suspend fun getSalesByDate(date: Date): Flow<List<Sales>>
}