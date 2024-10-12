package com.larrykin.chemistpos.home.presentation.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.core.data.GetAllMedicinesResult
import com.larrykin.chemistpos.home.data.Medicine
import com.larrykin.chemistpos.home.domain.MedicineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MedicineResult {
    data object Success : MedicineResult()
    data class Error(val message: String) : MedicineResult()
    data object MedicineExists : MedicineResult()
    data object MedicineNotFound : MedicineResult()
}

@HiltViewModel
class MedicineViewModel @Inject constructor(
    private val medicineRepository: MedicineRepository
) : ViewModel() {
    // hold states
    var name by mutableStateOf("")
    var company by mutableStateOf("")

    // create medicine
    fun createMedicine(onResult: (MedicineResult) -> Unit) {
        viewModelScope.launch {
            try {
                val nameTrim = name.trim()
                val companyTrim = company.trim()

                val medicine = Medicine(
                    name = nameTrim,
                    company = companyTrim
                )
                val result = medicineRepository.insertMedicine(medicine)
                if (result != null) {
                    onResult(MedicineResult.Success)
                } else {
                    onResult(MedicineResult.MedicineExists)
                }
            } catch (e: Exception) {
                onResult(MedicineResult.Error(e.message ?: "Unknown error occurred"))
            }
        }
    }

    // update medicine
    fun updateMedicine(onResult: (MedicineResult) -> Unit) {
        viewModelScope.launch {
            try {
                val nameTrim = name.trim()
                val companyTrim = company.trim()

                val medicineExists = medicineRepository.getMedicineByName(nameTrim)
                if (medicineExists == null) {
                    onResult(MedicineResult.MedicineNotFound)
                    return@launch
                }

                val medicine = Medicine(
                    id = medicineExists.id,
                    name = nameTrim,
                    company = companyTrim
                )
                val result = medicineRepository.updateMedicine(medicine)
                if (result != null) {
                    onResult(MedicineResult.Success)
                } else {
                    onResult(MedicineResult.Error("Unknown error updating medicine"))
                }
            } catch (e: Exception) {
                onResult(MedicineResult.Error(e.message ?: "Unknown error"))
            }
        }
    }

    // get medicines
    fun getMedicines(onResult: (List<Medicine>) -> Unit) {
        viewModelScope.launch {
            medicineRepository.getMedicines().collect { result ->
                when (result) {
                    is GetAllMedicinesResult.Success -> onResult(result.medicines)
                    is GetAllMedicinesResult.Error -> onResult(emptyList())
                }
            }
        }
    }

    // delete medicine
    fun deleteMedicine(medicineId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = medicineRepository.deleteMedicine(medicineId)
                if (result) {
                    // Refresh the UI by fetching the updated list of medicines
                    getMedicines { medicines ->
                        onResult(medicines.isNotEmpty())
                    }
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}