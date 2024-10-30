package com.larrykin.chemistpos.home.data

import com.larrykin.chemistpos.home.domain.IncomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IncomeRepositoryImplementation @Inject constructor(
    private val incomeDao: IncomeDao
) : IncomeRepository {
    override suspend fun insertIncome(income: Income): Long? {
        return try {
            incomeDao.insert(income)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getAllIncome(): Flow<List<Income>> {
        return incomeDao.getAllIncome()
    }

    override suspend fun getFirstIncome(): Income? {
        return incomeDao.getFirstIncome()
    }

    override suspend fun updateIncome(income: Income): Int {
        return try {
            val firstIncome = incomeDao.getFirstIncome()
            if (firstIncome != null) {
                val updatedIncome = income.copy(
                    id = firstIncome.id,
                    cash = firstIncome.cash + income.cash,
                    mpesa = firstIncome.mpesa + income.mpesa,
                    stockWorth = income.stockWorth,
                    servicesCash = firstIncome.servicesCash + income.servicesCash,
                    servicesMpesa = firstIncome.servicesMpesa + income.servicesMpesa,
                    profit = firstIncome.profit + income.profit,
                    loss = firstIncome.loss + income.loss
                )
                incomeDao.update(updatedIncome)
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }
}