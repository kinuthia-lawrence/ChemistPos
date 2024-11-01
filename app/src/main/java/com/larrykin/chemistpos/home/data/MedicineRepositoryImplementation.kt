package com.larrykin.chemistpos.home.data

import com.larrykin.chemistpos.core.data.GetAllMedicinesResult
import com.larrykin.chemistpos.home.domain.MedicineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MedicineRepositoryImplementation @Inject constructor(private val medicineDao: MedicineDao) :
    MedicineRepository {
        //insert medicine
    override suspend fun insertMedicine(medicine: Medicine): Long? {
        return try {
            medicine.timestamp = System.currentTimeMillis()
            medicineDao.insert(medicine)
        } catch (e: Exception) {
            -1
        }
    }

    //get all medicines
    override suspend fun getMedicines(): Flow<GetAllMedicinesResult> {
        return try {
            medicineDao.getAllMedicines().map { medicines ->
                if (medicines.isEmpty()) {
                    GetAllMedicinesResult.Success(emptyList())
                } else {
                    GetAllMedicinesResult.Success(medicines)
                }
            }
        } catch (e: Exception) {
            flowOf(GetAllMedicinesResult.Error(e.message ?: "An error occurred"))
        }
    }

    //get medicine by id
    override suspend fun getMedicineById(medicineId: Int): Medicine? {
        return try {
            medicineDao.getMedicineById(medicineId).firstOrNull()
        }catch (e: Exception){
            null
        }
    }

    //get medicine by name
    override suspend fun getMedicineByName(medicineName: String): Medicine? {
        return try {
            medicineDao.getMedicineByName(medicineName).firstOrNull()
        }catch (e: Exception){
            null
        }
    }

    //update medicine
    override suspend fun updateMedicine(medicine: Medicine): Medicine? {
        return try {
            medicine.timestamp = System.currentTimeMillis()
            val rowsUpdated = medicineDao.updateMedicine(medicine)
            if (rowsUpdated > 0) medicineDao.getMedicineById(medicine.id).firstOrNull() else null
        }catch (e: Exception){
            null
        }
    }

    //delete medicine
    override suspend fun deleteMedicine(medicineId: Int): Boolean {
        return try{
            medicineDao.deleteMedicine(medicineId) > 0
        }catch (e: Exception){
            false
        }
    }

    override suspend fun getAllCompanyNames(): List<String> {
        return medicineDao.getAllCompanyNames()
    }

    override suspend fun getAllMedicineNames(): List<String> {
        return medicineDao.getAllMedicineNames()
    }
}