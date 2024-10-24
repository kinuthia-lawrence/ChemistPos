package com.larrykin.chemistpos.home.presentation.viewModels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.raise.result
import com.larrykin.chemistpos.core.data.GetAllServicesResult
import com.larrykin.chemistpos.home.data.Service
import com.larrykin.chemistpos.home.domain.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ServiceResult {
    data object Success : ServiceResult()
    data class Error(val message: String) : ServiceResult()
    data object ServiceExists : ServiceResult()
    data object ServiceNotFound : ServiceResult()
}

@HiltViewModel
class ServicesViewModel @Inject constructor(private val servicesRepository: ServiceRepository) :
    ViewModel() {
    var name by mutableStateOf("")
    var description by mutableStateOf("")
    var price by mutableStateOf("")

    //create service
    fun createService(onResult: (ServiceResult) -> Unit) {
        viewModelScope.launch {
            try {
                val nameTrim = name.trim()
                val descriptionTrim = description.trim()

                val service = Service(
                    name = nameTrim,
                    description = descriptionTrim,
                    price = price.toDouble()
                )
                if (servicesRepository.getServiceByName(nameTrim) == null) {
                    val result = servicesRepository.insertService(service)
                    if (result != null) {
                        onResult(ServiceResult.Success)
                    } else {
                        onResult(ServiceResult.Error("An error occurred, Try again"))
                    }
                } else {
                    onResult(ServiceResult.ServiceExists)
                }
            } catch (e: Exception) {
                onResult(ServiceResult.Error(e.message ?: "Unknown error occurred"))
            }
        }
    }

    //update service
    fun updateService(updatedService: Service, serviceId: Int, onResult: (ServiceResult) -> Unit) {
        viewModelScope.launch {
            try {
                val serviceExists = servicesRepository.getServiceById(serviceId)
                if (serviceExists == null) {
                    onResult(ServiceResult.ServiceNotFound)
                    return@launch
                }

                val result = servicesRepository.updateService(updatedService)
                if (result != null) {
                    onResult(ServiceResult.Success)
                } else {
                    onResult(ServiceResult.Error("Unknown error updating service"))
                }
            } catch (e: Exception) {
                onResult(ServiceResult.Error(e.message ?: "Unknown error"))
            }
        }
    }

    //Get suppliers
    fun getServices(onResult: (List<Service>) -> Unit) {
        viewModelScope.launch {
            servicesRepository.getAllServices().collect { result ->
                when (result) {
                    is GetAllServicesResult.Success -> onResult(result.services)
                    is GetAllServicesResult.Error -> onResult(emptyList())
                }
            }
        }
    }

    //Delete service
    fun deleteService(serviceId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = servicesRepository.deleteService(serviceId)
                if (result) {
                    // Refresh the UI by fetching the updated list of suppliers
                    getServices { services ->
                        onResult(services.isNotEmpty())
                    }
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

}