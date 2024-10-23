package com.larrykin.chemistpos.home.domain

import com.larrykin.chemistpos.core.data.GetAllProductsResult
import com.larrykin.chemistpos.home.data.Product
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ProductRepository {
    suspend fun insertProduct(product: Product): Long?
    suspend fun getAllProducts(): Flow<GetAllProductsResult>
    suspend fun getProductById(productId: Int): Product?
    suspend fun getProductByName(productName: String): Product?
    suspend fun updateProduct(product: Product): Product?
    suspend fun deleteProduct(productId: Int): Boolean
    suspend fun getProductByNameCompanyFormulationExpiryDate(
        productName: String, company:
        String, formulation: String, expiryDate: Date
    ): Product?
}