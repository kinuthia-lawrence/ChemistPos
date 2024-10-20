package com.larrykin.chemistpos.home.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.core.data.GetAllProductsResult
import com.larrykin.chemistpos.home.data.Product
import com.larrykin.chemistpos.home.domain.MedicineRepository
import com.larrykin.chemistpos.home.domain.ProductRepository
import com.larrykin.chemistpos.home.domain.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StockResult {
    data object Success : StockResult()
    data class Error(val message: String) : StockResult()
}

@HiltViewModel
class StockViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val medicineRepository: MedicineRepository,
    private val supplierRepository: SupplierRepository
) : ViewModel() {
    // medicinesNames
    private val _medicineNames = MutableStateFlow<List<String>>(emptyList())
    val medicineNames: StateFlow<List<String>> get() = _medicineNames
    //companyNames
    private val _companyNames = MutableStateFlow<List<String>>(emptyList())
    val companyNames: StateFlow<List<String>> get() = _companyNames
    //supplierNames
    private val _supplierNames = MutableStateFlow<List<String>>(emptyList())
    val supplierNames: StateFlow<List<String>> get() = _supplierNames

    init {
        viewModelScope.launch{
            _medicineNames.value = medicineRepository.getAllMedicineNames()
            _companyNames.value = medicineRepository.getAllCompanyNames()
            _supplierNames.value = supplierRepository.getSupplierNames()
        }
    }
    //? Cart
    private val _cart = MutableStateFlow<List<Product>>(emptyList())
    val cart: StateFlow<List<Product>> get() = _cart
    //expected amount
    private val _expectedAmount = MutableStateFlow(0.0)
    val expectedAmount: StateFlow<Double> get() = _expectedAmount

    //function to set the expected amount
    fun setExpectedAmount(amount: Double) {
        _expectedAmount.value = amount
    }

    // Function to add a product to the cart
    fun addToCart(product: Product) {
        _cart.value = _cart.value + product
    }
    // Function to remove a product from the cart
    fun removeFromCart(product: Product, quantity: Int) {
        _cart.value = _cart.value - product
    }

    // Function to add a product to the stock
    suspend fun addProduct(product: Product, onResult: (StockResult) -> Unit) {
        viewModelScope.launch {
            try {
                val result = productRepository.insertProduct(product)
                if (result != null) {
                    if (result > 0) {
                        onResult(StockResult.Success)
                    } else {
                        onResult(StockResult.Error("Failed to add product"))
                    }
                }
            } catch (e: Exception) {
                onResult(StockResult.Error(e.message ?: "An error occurred"))
            }
        }
    }

    // Function to get all products from the stock
    suspend fun getAllProducts(onResult: (StockResult, List<Product>) -> Unit) {
        viewModelScope.launch {
            try {
                productRepository.getAllProducts().collect { result ->
                    when (result) {
                        is GetAllProductsResult.Success -> {
                            onResult(StockResult.Success, result.products)
                        }

                        is GetAllProductsResult.Error -> {
                            onResult(StockResult.Error(result.message), emptyList())
                        }
                    }
                }
            } catch (e: Exception) {
                onResult(StockResult.Error(e.message ?: "An error occurred"), emptyList())
            }
        }
    }

    // Function to delete a product from the stock
    suspend fun deleteProduct(productId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = productRepository.deleteProduct(productId)
                if (result) {
                    getAllProducts { stockResult, _ ->
                        onResult(stockResult is StockResult.Success)
                    }
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    // Function to update a product in the stock
    suspend fun updateProduct(product: Product, onResult: (StockResult) -> Unit) {
        viewModelScope.launch {
            try {
                val result = productRepository.updateProduct(product)
                if (result != null) {
                    getAllProducts { stockResult, _ ->
                        onResult(stockResult)
                    }
                } else {
                    onResult(StockResult.Error("Failed to update product"))
                }
            } catch (e: Exception) {
                onResult(StockResult.Error(e.message ?: "Unknown error"))
            }
        }
    }
}