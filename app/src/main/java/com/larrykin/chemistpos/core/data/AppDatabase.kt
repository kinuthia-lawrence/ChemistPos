package com.larrykin.chemistpos.core.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.larrykin.chemistpos.authentication.data.User
import com.larrykin.chemistpos.authentication.data.UserDao
import com.larrykin.chemistpos.core.converters.Converters

@Database(
    entities =[User::class],
    version = 2,
//    autoMigrations = [
//        AutoMigration (from = 1, to = 2)
//    ],
    exportSchema = false
)
@TypeConverters(Converters::class) //specifying the type converter class
abstract class AppDatabase : RoomDatabase() {
    //define abstract methods with zero arguments and return an instance of the DAO class
    abstract fun userDao(): UserDao
}