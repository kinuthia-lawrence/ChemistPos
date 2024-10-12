package com.larrykin.chemistpos.home.data

import com.larrykin.chemistpos.core.data.GetAllMedicinesResult
import com.larrykin.chemistpos.home.domain.MedicineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MedicineRepositoryImplementation @Inject constructor(private val medicineDao: MedicineDao) :
    MedicineRepository {
    override suspend fun insertMedicine(medicine: Medicine): Long? {
        TODO("Not yet implemented")
    }

    override suspend fun getMedicines(): Flow<GetAllMedicinesResult> {
        TODO("Not yet implemented")
    }

    override suspend fun getMedicineById(medicineId: Int): Medicine? {
        TODO("Not yet implemented")
    }

    override suspend fun getMedicineByName(medicineName: String): Medicine? {
        TODO("Not yet implemented")
    }

    override suspend fun updateMedicine(medicine: Medicine): Medicine? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMedicine(medicineId: Int): Boolean {
        TODO("Not yet implemented")
    }
}