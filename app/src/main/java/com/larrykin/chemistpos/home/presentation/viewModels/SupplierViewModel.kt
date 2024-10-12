package com.larrykin.chemistpos.home.presentation.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.core.data.GetAllSuppliersResult
import com.larrykin.chemistpos.home.data.Supplier
import com.larrykin.chemistpos.home.domain.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SupplierResult {
    data object Success : SupplierResult()
    data class Error(val message: String) : SupplierResult()
    data object SupplierExists : SupplierResult()
    data object SupplierNotFound : SupplierResult()
}

@HiltViewModel
class SupplierViewModel @Inject constructor(
    private val supplierRepository: SupplierRepository
) : ViewModel() {
    var name by mutableStateOf("")
    var phone by mutableStateOf("")
    var email by mutableStateOf("")
    var medicines by mutableStateOf(listOf<String>())

    // Create supplier
    fun createSupplier(onResult: (SupplierResult) -> Unit) {
        viewModelScope.launch {
            try {
                val nameTrim = name.trim()
                val phoneTrim = phone.trim()
                val emailTrim = email.trim()

                val supplier = Supplier(
                    name = nameTrim,
                    phone = phoneTrim,
                    email = emailTrim,
                    medicines = medicines
                )
                val result = supplierRepository.insertSupplier(supplier)
                if (result != null) {
                    onResult(SupplierResult.Success)
                } else {
                    onResult(SupplierResult.SupplierExists)
                }
            } catch (e: Exception) {
                onResult(SupplierResult.Error(e.message ?: "Unknown error occurred"))
            }
        }
    }

    // Update supplier
    fun updateSupplier(updatedSupplier: Supplier, supplierId: Int, onResult: (SupplierResult) -> Unit) {
        viewModelScope.launch {
            try {
                val supplierExists = supplierRepository.getSupplierById(supplierId)
                if (supplierExists == null) {
                    onResult(SupplierResult.SupplierNotFound)
                    return@launch
                }

                val result = supplierRepository.updateSupplier(updatedSupplier)
                if (result != null) {
                    onResult(SupplierResult.Success)
                } else {
                    onResult(SupplierResult.Error("Unknown error updating supplier"))
                }
            } catch (e: Exception) {
                onResult(SupplierResult.Error(e.message ?: "Unknown error"))
            }
        }
    }

    // Get suppliers
    fun getSuppliers(onResult: (List<Supplier>) -> Unit) {
        viewModelScope.launch {
            supplierRepository.getAllSuppliers().collect { result ->
                when (result) {
                    is GetAllSuppliersResult.Success -> onResult(result.suppliers)
                    is GetAllSuppliersResult.Error -> onResult(emptyList())
                }
            }
        }
    }

    // Delete supplier
    fun deleteSupplier(supplierId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = supplierRepository.deleteSupplier(supplierId)
                if (result) {
                    // Refresh the UI by fetching the updated list of suppliers
                    getSuppliers { suppliers ->
                        onResult(suppliers.isNotEmpty())
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