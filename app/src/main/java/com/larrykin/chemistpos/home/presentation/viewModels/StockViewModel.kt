package com.larrykin.chemistpos.home.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
}