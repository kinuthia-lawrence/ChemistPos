package com.larrykin.chemistpos.home.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface MedicineDao{
    //    suspend keyword is used to make the function asynchronous, it is used to make the
    //    function run in a coroutine so that it doesn't block the main thread but does not
    //    return a flow since flow observes changes in a coroutine
    //insert product
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medicine: Medicine): Long

}
