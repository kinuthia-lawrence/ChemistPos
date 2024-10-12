package com.larrykin.chemistpos.home.data

import com.larrykin.chemistpos.home.domain.SupplierRepository
import javax.inject.Inject

class SupplierRepositoryImplementation @Inject constructor(private val supplierDao : SupplierDao) :
    SupplierRepository {
}