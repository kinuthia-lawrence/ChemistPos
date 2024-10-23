package com.larrykin.chemistpos.home.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ProductDao {
    //    suspend keyword is used to make the function asynchronous, it is used to make the function run in a coroutine so that it doesn't block the main thread
    //insert product
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product): Long

    //get all products
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    //get product by id
    @Query("SELECT * FROM  products WHERE  id=:productId")
    fun getProductById(productId: Int): Flow<Product>

    //get product by name
    @Query("SELECT * FROM  products WHERE  name=:productName")
    fun getProductByName(productName: String): Flow<Product>

    //update a product by id
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProduct(product: Product): Int


    //delete a product
    @Query("DELETE FROM products WHERE id=:productId")
    suspend fun deleteProduct(productId: Int): Int

    //get product by name, company, formulation and expiry date
    @Query(
        "SELECT * FROM products WHERE name=:productName AND company=:company AND " +
                "formulation=:formulation AND DATE(expiry_date / 1000, 'unixepoch') = DATE(:expiryDate / 1000, 'unixepoch') LIMIT 1"
    )
    suspend fun getProductByNameCompanyFormulationExpiryDate(
        productName: String,
        company: String,
        formulation: String,
        expiryDate: Long
    ): Product?

}