package com.larrykin.chemistpos.home.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao{
    //    suspend keyword is used to make the function asynchronous, it is used to make the
    //    function run in a coroutine so that it doesn't block the main thread but does not
    //    return a flow since flow observes changes in a coroutine
    //insert product
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medicine: Medicine): Long

    //get all medicines
    @Query("SELECT * FROM medicines")
    fun getAllMedicines(): Flow<List<Medicine>>

    //get medicine by id
    @Query("SELECT * FROM  medicines WHERE  id=:medicineId")
    fun getMedicineById(medicineId: Int): Flow<Medicine>

    //get medicine by name
    @Query("SELECT * FROM  medicines WHERE  name=:medicineName")
    fun getMedicineByName(medicineName: String): Flow<Medicine>

    //update a medicine by id
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMedicine(medicine: Medicine): Int

    //delete a medicine
    @Query("DELETE FROM medicines WHERE id=:medicineId")
    suspend fun deleteMedicine(medicineId: Int): Int

    //get all medicine names
    @Query("SELECT name FROM medicines")
    suspend fun getAllMedicineNames(): List<String>

    //get all company names
    @Query("SELECT DISTINCT company FROM medicines")
    suspend fun getAllCompanyNames(): List<String>


}
