package com.larrykin.chemistpos.home.data

import com.larrykin.chemistpos.home.domain.SalesRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class SalesRepositoryImplementation @Inject constructor(
    private val salesDao: SalesDao
): SalesRepository {
    override suspend fun insertSale(sales: Sales): Long? {
        return try{
            salesDao.insertSale(sales)
        }catch (e: Exception){
            null
        }
    }

    override suspend fun getAllSales(): Flow<List<Sales>> {
        return salesDao.getAllSales()
    }

    override suspend fun getSalesByDate(date: Date): Flow<List<Sales>> {
        return salesDao.getSalesByDate(date)
    }
}