package com.larrykin.chemistpos.home.domain

import com.larrykin.chemistpos.core.data.GetAllServicesOfferedResult
import com.larrykin.chemistpos.core.data.GetAllServicesResult
import com.larrykin.chemistpos.core.data.GetAllSuppliersResult
import com.larrykin.chemistpos.home.data.Service
import com.larrykin.chemistpos.home.data.ServicesOffered
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    //! Services section
    suspend fun insertService(service: Service) : Long?
    suspend fun getAllServices() : Flow<GetAllServicesResult>
    suspend fun getServiceById(serviceId: Int) : Service?
    suspend fun getServiceByName(serviceName: String) : Service?
    suspend fun updateService(service: Service) : Service?
    suspend fun deleteService(serviceId: Int) : Boolean
    suspend fun getServiceNames() : List<String>

    //! Services Offered section
    suspend fun insertServiceOffered(serviceOffered: ServicesOffered) : Long?
    suspend fun getAllServicesOffered() : Flow<GetAllServicesOfferedResult>
    suspend fun getServiceOfferedById(serviceOfferedId: Int) : ServicesOffered?
    suspend fun updateServiceOffered(serviceOffered: ServicesOffered) : ServicesOffered?
    suspend fun deleteServiceOffered(serviceOfferedId: Int) : Boolean
}