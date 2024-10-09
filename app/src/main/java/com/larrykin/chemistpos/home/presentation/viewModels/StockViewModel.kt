package com.larrykin.chemistpos.home.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.larrykin.chemistpos.home.domain.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StockViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {
}