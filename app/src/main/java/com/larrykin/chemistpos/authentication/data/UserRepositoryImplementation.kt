package com.larrykin.chemistpos.authentication.data


import com.larrykin.chemistpos.authentication.domain.UserRepository
import javax.inject.Inject

//implements UserRepository
class UserRepositoryImplementation @Inject constructor(private val userDao: UserDao) :
    UserRepository {
    override suspend fun insertUser(user: User) {
        userDao.updateUser(user)
    }

    override suspend fun loginUser(username: String, password: String): User? {
        return userDao.getUserByUsernameAndPassword(username, password)
    }
}