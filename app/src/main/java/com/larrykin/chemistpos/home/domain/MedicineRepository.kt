package com.larrykin.chemistpos.home.domain

import com.larrykin.chemistpos.core.data.GetAllMedicinesResult
import com.larrykin.chemistpos.home.data.Medicine
import kotlinx.coroutines.flow.Flow

interface MedicineRepository {
    suspend fun insertMedicine(medicine: Medicine): Long?
    suspend fun getMedicines(): Flow<GetAllMedicinesResult>
    suspend fun getMedicineById(medicineId: Int): Medicine?
    suspend fun getMedicineByName(medicineName: String): Medicine?
    suspend fun updateMedicine(medicine: Medicine): Medicine?
    suspend fun deleteMedicine(medicineId: Int): Boolean
    suspend fun getAllCompanyNames() : List<String>
    suspend fun getAllMedicineNames() : List<String>
}