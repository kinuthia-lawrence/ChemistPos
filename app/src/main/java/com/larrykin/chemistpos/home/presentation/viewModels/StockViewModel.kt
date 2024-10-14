package com.larrykin.chemistpos.home.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.core.data.GetAllProductsResult
import com.larrykin.chemistpos.home.data.Product
import com.larrykin.chemistpos.home.domain.ProductRepository
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
    private val repository: ProductRepository
) : ViewModel() {
    private val _cart = MutableStateFlow<List<Product>>(emptyList())
    val cart: StateFlow<List<Product>> get() = _cart

    fun addToCart(product: Product) {
        _cart.value = _cart.value + product
    }

    suspend fun addProduct(product: Product, onResult: (StockResult) -> Unit) {
        viewModelScope.launch {
            try {
                val result = repository.insertProduct(product)
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

    suspend fun getAllProducts(onResult: (StockResult, List<Product>) -> Unit) {
        viewModelScope.launch {
            try {
                repository.getAllProducts().collect { result ->
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

    suspend fun deleteProduct(productId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = repository.deleteProduct(productId)
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

    suspend fun updateProduct(product: Product, onResult: (StockResult) -> Unit) {
        viewModelScope.launch {
            try {
                val result = repository.updateProduct(product)
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