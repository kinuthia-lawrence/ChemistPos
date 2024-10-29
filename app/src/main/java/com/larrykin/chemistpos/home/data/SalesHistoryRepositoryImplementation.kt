package com.larrykin.chemistpos.home.data

import com.larrykin.chemistpos.home.domain.SalesHistoryRepository
import javax.inject.Inject

class SalesHistoryRepositoryImplementation @Inject constructor(
    private val salesHistoryDao: SalesHistoryDao
) : SalesHistoryRepository {
}