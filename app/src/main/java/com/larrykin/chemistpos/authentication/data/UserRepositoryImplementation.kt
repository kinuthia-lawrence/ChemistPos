package com.larrykin.chemistpos.authentication.data

import android.util.Log
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.firestore.FirebaseFirestore
import com.larrykin.chemistpos.authentication.domain.UserRepository
import com.larrykin.chemistpos.core.data.AuthResult
import com.larrykin.chemistpos.core.data.GetAllUsersResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class UserRepositoryImplementation @Inject constructor(private val userDao: UserDao) :
    UserRepository {
    //insert user into the database
    override suspend fun insertUser(user: User): User? {
        return try {
            val existingUser = userDao.getUserByEmail(user.email).firstOrNull()
            if (existingUser != null) {
                return null
            } else {
                user.timestamp = System.currentTimeMillis()
                userDao.insert(user)
                userDao.getUserByEmail(user.email).firstOrNull()
            }
        } catch (e: Exception) {
            null
        }
    }

    //login user
    override suspend fun loginUser(username: String, password: String): User? {
        return try {
            userDao.getUserByUsernameAndPassword(username, password)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun loginUserByEmail(email: String, password: String): User? {
        return try {
            userDao.getUserByEmailAndPassword(email, password)
        } catch (e: Exception) {
            null
        }
    }

    //get user by email
    override suspend fun getUserByEmail(email: String): User? {
        return try {
            userDao.getUserByEmail(email).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    //get all users
    override suspend fun getAllUsers(): Flow<GetAllUsersResult> {
        return try {
            userDao.getAllUsers().map { users ->
                if (users.isEmpty()) {
                    GetAllUsersResult.Success(emptyList())
                } else {
                    GetAllUsersResult.Success(users)
                }
            }
        } catch (e: Exception) {
            flowOf(GetAllUsersResult.Error(e.message ?: "Unknown error"))
        }
    }
    /*    viewModelScope.launch {
        repository.getAllUsers().collect { result ->
            when (result) {
                is GetAllUsersResult.Success -> {
                    if (result.users.isEmpty()) {
                        // Handle the case where there are no users
                    } else {
                        // Handle the case where users are successfully fetched
                    }
                }
                is GetAllUsersResult.Error -> {
                    // Handle the error case
                }
            }
        }
    }*/

    //update user
    override suspend fun updateUser(user: User): User? {
        return try {
            user.timestamp = System.currentTimeMillis()
            val rowsUpdated = userDao.updateUser(user)
            if (rowsUpdated > 0) userDao.getUserByEmail(user.email).firstOrNull() else null
        } catch (e: Exception) {
            null
        }
    }

    //delete user
    override suspend fun deleteUser(user: User): AuthResult<String> {
        return try {
            val rowsDeleted = userDao.deleteUser(user)
            if (rowsDeleted > 0) {
                AuthResult.Success("User deleted successfully")
            } else {
                AuthResult.Error("User Deletion failed")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun updateProfilePicture(email: String, newUrl: String): User? {
        return try {
            val user = userDao.getUserByEmail(email).firstOrNull()
            if (user != null) {
                user.profilePictureUrl = newUrl
                user.timestamp = System.currentTimeMillis()
                userDao.updateUser(user)
                userDao.getUserByEmail(email).firstOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createUserInFirestore(user: User): Boolean {
        val firestore = FirebaseFirestore.getInstance()
        val userDocRef = firestore.collection("users").document(user.email)
        val taskCompletionSource = TaskCompletionSource<Boolean>()

        userDocRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val initialData = hashMapOf(
                    "email" to user.email,
                    "password" to user.password,
                    "userID" to user.email,
                    "subscription_status" to "active",
                    "subscription_expiry_date" to Date()
                )

                userDocRef.set(initialData).addOnSuccessListener {
                    // Create UserData collection with sub-collections
                    val userDataDocRef = firestore.collection("UserData").document(user.email)
                    val subCollections = listOf(
                        "income",
                        "medicines",
                        "products",
                        "sales",
                        "sales_history",
                        "services",
                        "services_offered",
                        "suppliers",
                        "users"
                    )
                    for (subCollection in subCollections) {
                        userDataDocRef.collection(subCollection)
                            .add(hashMapOf("initialized" to true))
                    }
                    Log.d("MyLogs", "Firestore: User document and UserData created successfully")
                    taskCompletionSource.setResult(true)
                }.addOnFailureListener { e ->
                    // Handle the error
                    Log.e("MyLogs", "Firestore: Error creating user document", e)
                    taskCompletionSource.setResult(false)
                }
            } else {
                taskCompletionSource.setResult(false)
            }
        }.addOnFailureListener { e ->
            // Handle the error
            Log.e("MyLogs", "Firestore: Error checking user document", e)
            taskCompletionSource.setResult(false)
        }

        return taskCompletionSource.task.await()
    }

    /*      //   in view model
         when (val result = repository.insertUser(user)) {
             is Result.Success -> onResult(RegisterResult.Success)
             is Result.Error -> onResult(RegisterResult.Error(result.message))
         }*/
}