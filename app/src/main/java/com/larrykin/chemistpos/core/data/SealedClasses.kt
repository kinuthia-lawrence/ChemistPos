package com.larrykin.chemistpos.core.data

import com.larrykin.chemistpos.authentication.data.User
import com.larrykin.chemistpos.home.data.Product

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