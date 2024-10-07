package com.larrykin.chemistpos.authentication.domain

import com.larrykin.chemistpos.core.data.AuthResult
import com.larrykin.chemistpos.core.data.GetAllUsersResult
import com.larrykin.chemistpos.authentication.data.User
import kotlinx.coroutines.flow.Flow


//The repository class is used to manage the data of the application
//It provides a clean API to the rest of the application
// In spring boot, its like the service layer
interface UserRepository{
    suspend fun insertUser(user: User): User?
    suspend fun loginUser(username: String, password:String): User?
    suspend fun loginUserByEmail(email: String, password: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun getAllUsers(): Flow<GetAllUsersResult>
    suspend fun updateUser(user: User): User?
    suspend fun deleteUser(user: User): AuthResult<String>
    suspend fun updateProfilePicture(email: String, newUrl: String): User?
}