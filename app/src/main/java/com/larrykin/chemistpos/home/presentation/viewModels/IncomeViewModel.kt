package com.larrykin.chemistpos.home.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.larrykin.chemistpos.home.domain.IncomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val incomeRepository: IncomeRepository
): ViewModel(){
}