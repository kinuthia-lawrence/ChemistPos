package com.larrykin.chemistpos.home.data

import com.larrykin.chemistpos.home.domain.SalesHistoryRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class SalesHistoryRepositoryImplementation @Inject constructor(
    private val salesHistoryDao: SalesHistoryDao
) : SalesHistoryRepository {
    override suspend fun insertSalesHistory(salesHistory: SalesHistory): Long? {
        return try {
            salesHistory.timestamp = System.currentTimeMillis()
            salesHistoryDao.insert(salesHistory)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getAllSalesHistory(): Flow<List<SalesHistory>> {
        return salesHistoryDao.getAllSalesHistory()
    }

    override suspend fun getSalesHistoryByDate(date: Date): Flow<List<SalesHistory>> {
        return salesHistoryDao.getSalesHistoryByDate(date)
    }
}