package com.larrykin.chemistpos.core.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.larrykin.chemistpos.authentication.data.User
import com.larrykin.chemistpos.authentication.data.UserDao
import com.larrykin.chemistpos.core.converters.Converters
import com.larrykin.chemistpos.home.data.Medicine
import com.larrykin.chemistpos.home.data.MedicineDao
import com.larrykin.chemistpos.home.data.Product
import com.larrykin.chemistpos.home.data.ProductDao
import com.larrykin.chemistpos.home.data.Sales
import com.larrykin.chemistpos.home.data.SalesDao
import com.larrykin.chemistpos.home.data.Service
import com.larrykin.chemistpos.home.data.ServicesDao
import com.larrykin.chemistpos.home.data.ServicesOffered
import com.larrykin.chemistpos.home.data.Supplier
import com.larrykin.chemistpos.home.data.SupplierDao

@Database(
    entities =[User::class, Product::class, Supplier::class, Medicine::class, Sales::class,
        Service::class, ServicesOffered::class],
    version = 12,
    exportSchema = false
)
@TypeConverters(Converters::class) //specifying the type converter class
abstract class AppDatabase : RoomDatabase() {
    //define abstract methods with zero arguments and return an instance of the DAO class
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun supplierDao(): SupplierDao
    abstract fun medicineDao(): MedicineDao
    abstract fun salesDao() : SalesDao
    abstract fun servicesDao() : ServicesDao
}