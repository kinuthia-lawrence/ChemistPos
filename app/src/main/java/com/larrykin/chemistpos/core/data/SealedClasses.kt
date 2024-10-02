package com.larrykin.chemistpos.core.data

import com.larrykin.chemistpos.authentication.data.User

sealed class AuthResult<out T> {
    data class Success<out T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}
sealed class GetAllUsersResult {
    data class Success(val users: List<User>) : GetAllUsersResult()
    data class Error(val message: String) : GetAllUsersResult()
}