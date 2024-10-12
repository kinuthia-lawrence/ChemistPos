package com.larrykin.chemistpos.core.data

import com.larrykin.chemistpos.authentication.data.User
import com.larrykin.chemistpos.home.data.Medicine
import com.larrykin.chemistpos.home.data.Product
import com.larrykin.chemistpos.home.data.Supplier

sealed class AuthResult<out T> {
    data class Success<out T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}
sealed class GetAllUsersResult {
    data class Success(val users: List<User>) : GetAllUsersResult()
    data class Error(val message: String) : GetAllUsersResult()
}

sealed class GetAllProductsResult {
    data class Success(val products: List<Product>) : GetAllProductsResult()
    data class Error(val message: String) : GetAllProductsResult()
}

sealed class GetAllSuppliersResult {
    data class Success(val suppliers: List<Supplier>) : GetAllSuppliersResult()
    data class Error(val message: String) : GetAllSuppliersResult()
}
 sealed class  GetAllMedicinesResult {
    data class Success(val medicines: List<Medicine>) : GetAllMedicinesResult()
    data class Error(val message: String) : GetAllMedicinesResult()
}