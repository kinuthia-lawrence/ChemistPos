package com.larrykin.chemistpos.home.data

import com.larrykin.chemistpos.core.data.GetAllSuppliersResult
import com.larrykin.chemistpos.home.domain.SupplierRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SupplierRepositoryImplementation @Inject constructor(private val supplierDao : SupplierDao) :
    SupplierRepository {
    override suspend fun insertSupplier(supplier: Supplier): Long? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllSuppliers(): Flow<GetAllSuppliersResult> {
        TODO("Not yet implemented")
    }

    override suspend fun getSupplierById(supplierId: Int): Supplier? {
        TODO("Not yet implemented")
    }

    override suspend fun getSupplierByName(supplierName: String): Supplier? {
        TODO("Not yet implemented")
    }

    override suspend fun updateSupplier(supplier: Supplier): Supplier? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSupplier(supplierId: Int): Boolean {
        TODO("Not yet implemented")
    }
}