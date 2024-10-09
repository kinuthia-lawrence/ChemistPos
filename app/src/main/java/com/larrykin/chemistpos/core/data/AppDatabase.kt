package com.larrykin.chemistpos.core.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.larrykin.chemistpos.authentication.data.User
import com.larrykin.chemistpos.authentication.data.UserDao
import com.larrykin.chemistpos.core.converters.Converters
import com.larrykin.chemistpos.home.data.Product
import com.larrykin.chemistpos.home.data.ProductDao

@Database(
    entities =[User::class, Product::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class) //specifying the type converter class
abstract class AppDatabase : RoomDatabase() {
    //define abstract methods with zero arguments and return an instance of the DAO class
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
}