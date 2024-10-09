package com.larrykin.chemistpos.home.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.core.data.GetAllProductsResult
import com.larrykin.chemistpos.home.data.Product
import com.larrykin.chemistpos.home.domain.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//sealed classes are used to represent restricted class hierarchies, when a value can have one of the types from a limited set, but cannot have any other type.
sealed class StockResult {
    data object Success : StockResult()
    data class Error(val message: String) : StockResult()
}

@HiltViewModel
class StockViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {
    //suspend is a keyword that is used to tell the compiler that this function will be used in a coroutine

    //inserts a product into the database
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

    //gets all products from the database
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

    // Delete product from the database
    suspend fun deleteProduct(productId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = repository.deleteProduct(productId)
                if (result) {
                    // Refresh the UI by fetching the updated list of products
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

    // Update product in the database
    suspend fun updateProduct(product: Product, onResult: (StockResult) -> Unit) {
        viewModelScope.launch {
            try {
                val result = repository.updateProduct(product)
                if (result != null) {
                    // Refresh the UI by fetching the updated list of products
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