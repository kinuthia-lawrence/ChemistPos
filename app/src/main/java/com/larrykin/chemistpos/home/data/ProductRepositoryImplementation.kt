package com.larrykin.chemistpos.home.data

import com.larrykin.chemistpos.core.data.GetAllProductsResult
import com.larrykin.chemistpos.home.domain.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ProductRepositoryImplementation @Inject constructor(private val productDao: ProductDao) :
    ProductRepository {
    //insert product into the database
    override suspend fun insertProduct(product: Product): Long {
        return try {
            productDao.insert(product)
        } catch (e: Exception) {
            -1
        }
    }

    //get all products
    override suspend fun getAllProducts(): Flow<GetAllProductsResult> {
        return try {
            productDao.getAllProducts().map { products ->
                if (products.isEmpty()) {
                    GetAllProductsResult.Success(emptyList())
                } else {
                    GetAllProductsResult.Success(products)
                }
            }
        } catch (e: Exception) {
            flowOf(GetAllProductsResult.Error(e.message ?: "An error occurred"))
        }
    }

    //get product by id
    override suspend fun getProductById(productId: Int): Product? {
        return try {
            productDao.getProductById(productId).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    //get product by name
    override suspend fun getProductByName(productName: String): Product? {
        return try {
            productDao.getProductByName(productName).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateProduct(product: Product): Product? {
        return try {
            val rowsUpdated = productDao.updateProduct(product)
            if (rowsUpdated > 0) productDao.getProductById(product.id).firstOrNull() else null
        } catch (e: Exception) {
            null
        }
    }

    //delete a product
    override suspend fun deleteProduct(productId: Int): Boolean {
        return try {
            //delete product from the database
            productDao.deleteProduct(productId)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getProductByNameCompanyFormulationExpiryDate(
        productName: String,
        company: String,
        formulation: String,
        expiryDate: Date
    ): Product? {
        return try {
            val expiryDateTimestamp = expiryDate.time
            productDao.getProductByNameCompanyFormulationExpiryDate(
                productName,
                company,
                formulation,
                expiryDateTimestamp
            )
        } catch (e: Exception) {
            null
        }
    }


}