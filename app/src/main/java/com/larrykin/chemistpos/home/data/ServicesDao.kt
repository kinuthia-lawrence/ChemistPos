package com.larrykin.chemistpos.home.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ServicesDao {
    //! Services Section
    // Insert a service
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: Service) : Long

    // Get all services
    @Query("SELECT * FROM services")
    fun getAllServices(): Flow<List<Service>>

    // Get a service by id
    @Query("SELECT * FROM services WHERE id=:serviceId")
    fun getServiceById(serviceId: Int): Flow<Service>

    //Get a service by name
    @Query("SELECT *  FROM services  WHERE name=:serviceName")
    fun getServiceByName(serviceName: String): Flow<Service>

    //update a service
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateService(service: Service): Int

    //delete a service
    @Query("DELETE FROM services WHERE id=:serviceId")
    suspend fun deleteService(serviceId: Int): Int

    //get service name
    @Query("SELECT name FROM  services")
    suspend fun getAllServiceNames(): List<String>

    //! Services Offered Section
    // Insert a service offered
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceOffered(serviceOffered: ServicesOffered) : Long

    // Get all services offered
    @Query("SELECT * FROM services_offered")
    fun getAllServicesOffered(): Flow<List<ServicesOffered>>

    // Get a service offered by id
    @Query("SELECT * FROM services_offered WHERE id=:serviceOfferedId")
    fun getServiceOfferedById(serviceOfferedId: Int): Flow<ServicesOffered>

    //update a service offered
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateServiceOffered(serviceOffered: ServicesOffered): Int

    //delete a service offered
    @Query("DELETE FROM services_offered WHERE id=:serviceOfferedId")
    suspend fun deleteServiceOffered(serviceOfferedId: Int): Int

    //get all service offered names
    @Query("SELECT name FROM services_offered")
    suspend fun getAllServiceOfferedNames(): List<String>
}