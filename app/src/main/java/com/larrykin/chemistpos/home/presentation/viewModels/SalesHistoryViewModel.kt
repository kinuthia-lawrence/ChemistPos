package com.larrykin.chemistpos.home.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.larrykin.chemistpos.home.data.SalesHistoryDao
import javax.inject.Inject

class SalesHistoryViewModel @Inject constructor(
    private val salesHistoryDao: SalesHistoryDao
) : ViewModel() {
}