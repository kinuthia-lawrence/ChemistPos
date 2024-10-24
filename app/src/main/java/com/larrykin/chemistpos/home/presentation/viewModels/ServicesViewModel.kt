package com.larrykin.chemistpos.home.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.larrykin.chemistpos.home.domain.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

}