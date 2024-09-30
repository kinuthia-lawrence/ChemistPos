package com.larrykin.chemistpos.authentication.domain

import com.larrykin.chemistpos.authentication.data.User


//The repository class is used to manage the data of the application
//It provides a clean API to the rest of the application
// In spring boot, its like the service layer
interface UserRepository{
    suspend fun insertUser(user: User)
    suspend fun loginUser(username: String, password:String): User?
}