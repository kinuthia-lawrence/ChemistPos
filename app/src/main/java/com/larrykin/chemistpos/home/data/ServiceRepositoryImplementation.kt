package com.larrykin.chemistpos.home.data

import com.larrykin.chemistpos.core.data.GetAllServicesOfferedResult
import com.larrykin.chemistpos.core.data.GetAllServicesResult
import com.larrykin.chemistpos.home.domain.ServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ServiceRepositoryImplementation @Inject constructor(
    private val servicesDao: ServicesDao
) : ServiceRepository {
    //! Services section
    //insert service
    override suspend fun insertService(service: Service): Long? {
        return try {
            service.timestamp = System.currentTimeMillis()
            servicesDao.insertService(service)
        } catch (e: Exception) {
            -1
        }
    }

    //get all services
    override suspend fun getAllServices(): Flow<GetAllServicesResult> {
        return try {
            servicesDao.getAllServices().map { services ->
                if (services.isEmpty()) {
                    GetAllServicesResult.Success(emptyList())
                } else {
                    GetAllServicesResult.Success(services)
                }
            }
        } catch (e: Exception) {
            flowOf(GetAllServicesResult.Error(e.message ?: "An error occurred"))
        }
    }

    //get service by id
    override suspend fun getServiceById(serviceId: Int): Service? {
        return try {
            servicesDao.getServiceById(serviceId).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getServiceByName(serviceName: String): Service? {
        return try {
            servicesDao.getServiceByName(serviceName).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    //update service
    override suspend fun updateService(service: Service): Service? {
        return try {
            service.timestamp = System.currentTimeMillis()
            val rowsUpdated = servicesDao.updateService(service)
            if (rowsUpdated > 0) servicesDao.getServiceById(service.id).firstOrNull() else null
        } catch (e: Exception) {
            null
        }
    }

    //delete service
    override suspend fun deleteService(serviceId: Int): Boolean {
        return try {
            servicesDao.deleteService(serviceId) > 0
        } catch (e: Exception) {
            false
        }
    }

    //get service names
    override suspend fun getServiceNames(): List<String> {
        return try {
            servicesDao.getAllServiceNames()
        } catch (e: Exception) {
            emptyList()
        }
    }

    //! Services Offered section
    //insert service offered
    override suspend fun insertServiceOffered(serviceOffered: ServicesOffered): Long? {
        return try {
            serviceOffered.timestamp = System.currentTimeMillis()
            servicesDao.insertServiceOffered(serviceOffered)
        } catch (e: Exception) {
            -1
        }
    }

    //get all services offered
    override suspend fun getAllServicesOffered(): Flow<GetAllServicesOfferedResult> {
        return try {
            servicesDao.getAllServicesOffered().map { servicesOffered ->
                if (servicesOffered.isEmpty()) {
                    GetAllServicesOfferedResult.Success(emptyList())
                } else {
                    GetAllServicesOfferedResult.Success(servicesOffered)
                }
            }
        } catch (e: Exception) {
            flowOf(GetAllServicesOfferedResult.Error(e.message ?: "An error occurred"))
        }
    }

    //get service offered by id
    override suspend fun getServiceOfferedById(serviceOfferedId: Int): ServicesOffered? {
        return try {
            servicesDao.getServiceOfferedById(serviceOfferedId).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    //update service offered
    override suspend fun updateServiceOffered(serviceOffered: ServicesOffered): ServicesOffered? {
        return try {
            serviceOffered.timestamp = System.currentTimeMillis()
            val rowsUpdated = servicesDao.updateServiceOffered(serviceOffered)
            if (rowsUpdated > 0) servicesDao.getServiceOfferedById(serviceOffered.id)
                .firstOrNull() else null
        } catch (e: Exception) {
            null
        }
    }

    //delete service offered
    override suspend fun deleteServiceOffered(serviceOfferedId: Int): Boolean {
        return try {
            servicesDao.deleteServiceOffered(serviceOfferedId) > 0
        } catch (e: Exception) {
            false
        }
    }


}