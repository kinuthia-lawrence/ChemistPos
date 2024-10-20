package com.larrykin.chemistpos.home.data

import com.larrykin.chemistpos.core.data.GetAllSuppliersResult
import com.larrykin.chemistpos.home.domain.SupplierRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SupplierRepositoryImplementation @Inject constructor(private val supplierDao : SupplierDao) :
    SupplierRepository {
        //insert supplier
    override suspend fun insertSupplier(supplier: Supplier): Long? {
        return try{
            supplierDao.insert(supplier)
        }catch (e: Exception){
            -1
        }
    }

    //get all suppliers
    override suspend fun getAllSuppliers(): Flow<GetAllSuppliersResult> {
        return try{
            supplierDao.getAllSuppliers().map { suppliers ->
                if (suppliers.isEmpty()){
                    GetAllSuppliersResult.Success(emptyList())
                }else{
                    GetAllSuppliersResult.Success(suppliers)
                }
            }
        }catch (e: Exception){
            flowOf(GetAllSuppliersResult.Error(e.message ?: "An error occurred"))
        }
    }

    //get supplier by id
    override suspend fun getSupplierById(supplierId: Int): Supplier? {
        return try {
            supplierDao.getSupplierById(supplierId).firstOrNull()
        }catch (e: Exception){
            null
        }
    }

    //get supplier by name
    override suspend fun getSupplierByName(supplierName: String): Supplier? {
        return try {
            supplierDao.getSupplierByName(supplierName).firstOrNull()
        }catch (e: Exception){
            null
        }
    }

    //update supplier
    override suspend fun updateSupplier(supplier: Supplier): Supplier? {
        return try {
            val rowsUpdated = supplierDao.updateSupplier(supplier)
            if (rowsUpdated > 0) supplierDao.getSupplierById(supplier.id).firstOrNull() else null
        }catch (e: Exception){
            null
        }
    }

    //delete supplier
    override suspend fun deleteSupplier(supplierId: Int): Boolean {
        return try {
            supplierDao.deleteSupplier(supplierId) > 0
        }catch (e: Exception){
            false
        }
    }

    override suspend fun getSupplierNames(): List<String> {
        return supplierDao.getAllSupplierNames()
    }
}