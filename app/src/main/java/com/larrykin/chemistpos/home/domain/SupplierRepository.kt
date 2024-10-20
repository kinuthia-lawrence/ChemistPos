package com.larrykin.chemistpos.home.domain

import com.larrykin.chemistpos.core.data.GetAllSuppliersResult
import com.larrykin.chemistpos.home.data.Supplier
import kotlinx.coroutines.flow.Flow

interface SupplierRepository {
    suspend fun insertSupplier(supplier: Supplier) : Long?
    suspend fun getAllSuppliers() : Flow<GetAllSuppliersResult>
    suspend fun getSupplierById(supplierId: Int) : Supplier?
    suspend fun getSupplierByName(supplierName: String) : Supplier?
    suspend fun updateSupplier(supplier: Supplier) : Supplier?
    suspend fun deleteSupplier(supplierId: Int) : Boolean
    suspend fun  getSupplierNames() : List<String>
}