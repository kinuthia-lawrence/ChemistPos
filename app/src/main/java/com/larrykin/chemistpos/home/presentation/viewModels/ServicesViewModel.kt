package com.larrykin.chemistpos.home.presentation.viewModels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.core.data.GetAllServicesOfferedResult
import com.larrykin.chemistpos.core.data.GetAllServicesResult
import com.larrykin.chemistpos.home.data.Service
import com.larrykin.chemistpos.home.data.ServicesOffered
import com.larrykin.chemistpos.home.domain.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
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


    var serviceOfferedName by mutableStateOf("")
    var servitor by mutableStateOf("")
    var serviceOfferedDescription by mutableStateOf("")
    var serviceOfferedCash by mutableStateOf("")
    var serviceOfferedMpesa by mutableStateOf("")

    val serviceOfferedNameTrim = serviceOfferedName.trim()

    // medicinesNames
    private val _serviceNames = MutableStateFlow<List<String>>(emptyList())
    val serviceNames: StateFlow<List<String>> get() = _serviceNames

    //expected amount
    private val _expectedAmount = MutableStateFlow(0.0)
    val expectedAmount: StateFlow<Double> get() = _expectedAmount

    init {
        viewModelScope.launch {
            _serviceNames.value = servicesRepository.getServiceNames()
        }
    }

    //function to set the expected amount
    fun setExpectedAmount(name: String) {
        viewModelScope.launch {
            val nameTrim = name.trim()
            val serviceExists = servicesRepository.getServiceByName(nameTrim)
            if (serviceExists != null) {
                _expectedAmount.value = serviceExists.price
            } else {
                _expectedAmount.value = 0.0
            }
        }
    }

    //!Service Section
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

    //ServiceOffered Section
    //create service offered
    //create service
    fun createServiceOffered(onResult: (ServiceResult) -> Unit) {
        viewModelScope.launch {
            try {
                val servitorTrim = servitor.trim()
                val serviceOfferedDescriptionTrim = serviceOfferedDescription.trim()


                val servicesOffered = ServicesOffered(
                    name = serviceOfferedNameTrim,
                    servitor = servitorTrim,
                    description = serviceOfferedDescriptionTrim,
                    cash = serviceOfferedCash.toDoubleOrNull() ?: 0.0,
                    mpesa = serviceOfferedMpesa.toDoubleOrNull() ?: 0.0,
                    totalPrice = (serviceOfferedCash.toDoubleOrNull() ?: 0.0) +
                            (serviceOfferedMpesa.toDoubleOrNull() ?: 0.0),
                    expectedAmount = expectedAmount.value,
                    createdAt = Date(),
                    updatedAt = null
                )
                val result = servicesRepository.insertServiceOffered(servicesOffered)
                if (result != null) {
                    onResult(ServiceResult.Success)
                } else {
                    onResult(ServiceResult.Error("An error occurred, Try again"))
                }

            } catch (e: Exception) {
                onResult(ServiceResult.Error(e.message ?: "Unknown error occurred"))
            }
        }
    }

    //update service
    fun updateServiceOffered(
        updatedService: ServicesOffered, serviceId: Int, onResult: (ServiceResult) ->
        Unit
    ) {
        viewModelScope.launch {
            try {
                val serviceExists = servicesRepository.getServiceOfferedById(serviceId)
                if (serviceExists == null) {
                    onResult(ServiceResult.ServiceNotFound)
                    return@launch
                }

                val result = servicesRepository.updateServiceOffered(updatedService)
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
    fun getServicesOffered(onResult: (ServiceResult, List<ServicesOffered>) -> Unit) {
        viewModelScope.launch {
            servicesRepository.getAllServicesOffered().collect { result ->
                when (result) {
                    is GetAllServicesOfferedResult.Success -> onResult(
                        ServiceResult.Success, result
                            .servicesOffered
                    )

                    is GetAllServicesOfferedResult.Error -> onResult(
                        ServiceResult.Error(result.message),
                        emptyList()
                    )
                }
            }
        }
    }

    //Delete service
    fun deleteServiceOffered(serviceId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = servicesRepository.deleteServiceOffered(serviceId)
                if (result) {
                    // Refresh the UI by fetching the updated list of suppliers
                    getServicesOffered { serviceResult, _ ->
                        onResult(serviceResult is ServiceResult.Success)
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