package com.larrykin.chemistpos.home.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.home.data.Income
import com.larrykin.chemistpos.home.data.SalesHistory
import com.larrykin.chemistpos.home.domain.IncomeRepository
import com.larrykin.chemistpos.home.domain.SalesHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val salesHistoryRepository: SalesHistoryRepository
) : ViewModel() {

    private val _salesHistory = MutableStateFlow<List<SalesHistory>>(emptyList())
    val salesHistory: StateFlow<List<SalesHistory>> get() = _salesHistory

    private val _income = MutableStateFlow(Income(
        cash = 0.0,
        mpesa = 0.0,
        stockWorth = 0.0,
        servicesMpesa = 0.0,
        servicesCash = 0.0,
        profit = 0.0,
        loss = 0.0
    ))
    val income: StateFlow<Income> get() = _income

    init {
        fetchSalesHistory()
        fetchIncome()
    }

    private fun fetchSalesHistory() {
        viewModelScope.launch {
            salesHistoryRepository.getAllSalesHistory().collect { salesHistoryList ->
                _salesHistory.value = salesHistoryList
            }
        }
    }

    private fun fetchIncome() {
        viewModelScope.launch {
            val incomeResult = incomeRepository.getFirstIncome()
            if (incomeResult != null) {
                _income.value = incomeResult
            }
        }
    }
}