package com.larrykin.chemistpos.home.data

import com.larrykin.chemistpos.home.domain.MedicineRepository
import javax.inject.Inject

class MedicineRepositoryImplementation @Inject constructor(private val medicineDao: MedicineDao) :
    MedicineRepository {
}