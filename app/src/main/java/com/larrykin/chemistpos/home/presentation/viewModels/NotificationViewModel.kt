package com.larrykin.chemistpos.home.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.core.data.GetAllProductsResult
import com.larrykin.chemistpos.home.data.Product
import com.larrykin.chemistpos.home.domain.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _expiredGoods = MutableStateFlow<List<Product>>(emptyList())
    val expiredGoods: StateFlow<List<Product>> get() = _expiredGoods

    private val _outOfStockGoods = MutableStateFlow<List<Product>>(emptyList())
    val outOfStockGoods: StateFlow<List<Product>> get() = _outOfStockGoods

    fun getExpiredGoods() {
        viewModelScope.launch {
            try {

                productRepository.getAllProducts().collect { result ->
                    if (result is GetAllProductsResult.Success) {
                        val expiredGoodsList = result.products.filter { product ->
                            product.expiryDate.before(Date())
                        }
                        _expiredGoods.value = expiredGoodsList
                        Log.d("Set Logs", "getExpiredGoods: $expiredGoodsList")
                    }
                }
            } catch (e: Exception) {
                Log.d("Set Logs", "getExpiredGoods Error: ${e.message}")
            }
        }
    }

    fun getOutOfStockGoods() {
        viewModelScope.launch {
            try {
                productRepository.getAllProducts().collect { result ->
                    if (result is GetAllProductsResult.Success) {
                        val outOfStockGoodsList = result.products.filter { product ->
                            product.quantityAvailable < product.minStock
                        }
                        _outOfStockGoods.value = outOfStockGoodsList
                        Log.d("Set Logs", "getOutOfStockGoods: $outOfStockGoodsList")
                    }
                }
            } catch (e: Exception) {
                Log.d("Set Logs", "getOutOfStockGoods Error: ${e.message}")
            }
        }
    }

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            try {
                productRepository.deleteProduct(productId)
                Log.d("Set Logs", "deleteProduct: $productId")
            } catch (e: Exception) {
                Log.d("Set Logs", "deleteProduct Error: ${e.message}")
            }
        }
    }
}