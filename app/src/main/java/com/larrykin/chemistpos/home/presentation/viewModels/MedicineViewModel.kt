package com.larrykin.chemistpos.home.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.larrykin.chemistpos.home.domain.MedicineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MedicineViewModel @Inject constructor(
    private val medicineRepository: MedicineRepository
) : ViewModel() {
}