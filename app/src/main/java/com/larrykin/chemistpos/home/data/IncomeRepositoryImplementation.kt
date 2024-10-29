package com.larrykin.chemistpos.home.data

import com.larrykin.chemistpos.home.domain.IncomeRepository
import javax.inject.Inject

class IncomeRepositoryImplementation @Inject constructor(
    private val incomeDao: IncomeDao
) : IncomeRepository {
}