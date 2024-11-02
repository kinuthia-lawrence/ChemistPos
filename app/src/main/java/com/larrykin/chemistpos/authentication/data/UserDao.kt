package com.larrykin.chemistpos.authentication.data

import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.larrykin.chemistpos.home.data.Income
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.Continuation

//Data Access Objects are used to access the database, all the crud operations are defined here
//The @Dao annotation is used to tell room that this is a Dao class
@Dao
interface UserDao {
    //    suspend keyword is used to make the function asynchronous, it is used to make the function run in a coroutine so that it doesn't block the main thread
    //todo: check whether to use insert or upsert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    //insert all users
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: List<User>)

    //    @Upsert//combines both insertion and update. If the data is already present, it updates it, if not it inserts it(automatically handles the conflict)
    //    suspend fun upsertUser(user: User)

    //get users
    //flow is an observable data holder. it notifies the observer when the data changes(like LiveData)
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    //get user by email
    @Query("SELECT * FROM users WHERE email=:userEmail")
    fun getUserByEmail(userEmail: String): Flow<User>

    //get by username and password
    @Query("SELECT * FROM users WHERE username=:loginUsername AND password=:loginPassword")
    suspend fun getUserByUsernameAndPassword(loginUsername: String, loginPassword: String): User?

    //get by email and password
    @Query("SELECT * FROM users WHERE email=:loginEmail AND password=:loginPassword")
    suspend fun getUserByEmailAndPassword(loginEmail: String, loginPassword: String): User?

    //update User
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUser(user: User): Int

    //delete user
    @Delete
    suspend fun deleteUser(user: User): Int

    //update profile picture
    @Query("UPDATE users SET profile_picture_url = :newUrl WHERE email = :email")
    suspend fun updateProfilePicture(email: String, newUrl: String) : Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAll(users: List<User>)

}