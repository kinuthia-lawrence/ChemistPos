package com.larrykin.chemistpos.home.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SupplierDao {
    //    suspend keyword is used to make the function asynchronous, it is used to make the
    //    function run in a coroutine so that it doesn't block the main thread but does not
    //    return a flow since flow observes changes in a coroutine
    //insert product
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(supplier: Supplier): Long

    //get all suppliers
    @Query("SELECT * FROM suppliers")
    fun getAllSuppliers(): Flow<List<Supplier>>

    //get supplier by id
    @Query("SELECT * FROM  suppliers WHERE  id=:supplierId")
    fun getSupplierById(supplierId: Int): Flow<Supplier>

    //get supplier by name
    @Query("SELECT * FROM  suppliers WHERE  name=:supplierName")
    fun getSupplierByName(supplierName: String): Flow<Supplier>

    //update a supplier by id
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSupplier(supplier: Supplier): Int


    //delete a supplier
    @Query("DELETE FROM suppliers WHERE id=:supplierId")
    suspend fun deleteSupplier(supplierId: Int): Int

}
