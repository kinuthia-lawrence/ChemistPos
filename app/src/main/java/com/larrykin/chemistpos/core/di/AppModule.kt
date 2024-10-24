package com.larrykin.chemistpos.core.di

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.larrykin.chemistpos.authentication.data.UserDao
import com.larrykin.chemistpos.authentication.data.UserRepositoryImplementation
import com.larrykin.chemistpos.authentication.domain.UserRepository
import com.larrykin.chemistpos.core.data.AppDatabase
import com.larrykin.chemistpos.home.data.MedicineDao
import com.larrykin.chemistpos.home.data.MedicineRepositoryImplementation
import com.larrykin.chemistpos.home.data.ProductDao
import com.larrykin.chemistpos.home.data.ProductRepositoryImplementation
import com.larrykin.chemistpos.home.data.SalesDao
import com.larrykin.chemistpos.home.data.SalesRepositoryImplementation
import com.larrykin.chemistpos.home.data.ServiceRepositoryImplementation
import com.larrykin.chemistpos.home.data.ServicesDao
import com.larrykin.chemistpos.home.data.SupplierDao
import com.larrykin.chemistpos.home.data.SupplierRepositoryImplementation
import com.larrykin.chemistpos.home.domain.MedicineRepository
import com.larrykin.chemistpos.home.domain.ProductRepository
import com.larrykin.chemistpos.home.domain.SalesRepository
import com.larrykin.chemistpos.home.domain.ServiceRepository
import com.larrykin.chemistpos.home.domain.SupplierRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module // Annotates classes that define how dependencies are provided to the system
@InstallIn(SingletonComponent::class) // Installing AppModule to SingletonComponent
object AppModule {

    //migration after adding profile picture url
    private val MIGRATION_1_2 =
        object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE users ADD COLUMN 'profile_Picture_url' TEXT")
            }
        }

    //migration after adding products table
    private val MIGRATION_2_3 =
        object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                //create products table
                db.execSQL(
                    """
    CREATE TABLE IF NOT EXISTS `products` (
        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
        `name` TEXT NOT NULL, 
        `category` TEXT NOT NULL, 
        `min_quantity` INTEGER NOT NULL, 
        `quantity_available` INTEGER NOT NULL, 
        `buying_price` REAL NOT NULL, 
        `retail_selling_price` REAL NOT NULL, 
        `wholesale_selling_price` REAL NOT NULL, 
        `supplier_name` TEXT NOT NULL, 
        `date_added` INTEGER NOT NULL, 
        `expiry_date` INTEGER, 
        `description` TEXT
    )
"""
                )
            }
        }

    //migration after modifying products table
    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create the new table
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `products_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `company` TEXT NOT NULL,
                `formulation` TEXT NOT NULL,
                `min_stock` INTEGER NOT NULL,
                `min_measure` INTEGER NOT NULL,
                `quantity_available` INTEGER NOT NULL,
                `buying_price` REAL NOT NULL,
                `retail_selling_price` REAL NOT NULL,
                `wholesale_selling_price` REAL NOT NULL,
                `supplier_name` TEXT NOT NULL,
                `date_added` INTEGER NOT NULL,
                `expiry_date` INTEGER,
                `description` TEXT
            )
        """
            )

            // Copy the data from the old table to the new table
            database.execSQL(
                """
            INSERT INTO `products_new` (
                `id`, `name`, `company`, `formulation`, `min_stock`, `min_measure`, 
                `quantity_available`, `buying_price`, `retail_selling_price`, 
                `wholesale_selling_price`, `supplier_name`, `date_added`, 
                `expiry_date`, `description`
            )
            SELECT 
                `id`, `name`, '' AS `company`, '' AS `formulation`, `min_quantity` AS `min_stock`, 
                0 AS `min_measure`, `quantity_available`, `buying_price`, `retail_selling_price`, 
                `wholesale_selling_price`, `supplier_name`, `date_added`, 
                `expiry_date`, `description`
            FROM `products`
        """
            )

            // Remove the old table
            database.execSQL("DROP TABLE `products`")

            // Rename the new table to the old table name
            database.execSQL("ALTER TABLE `products_new` RENAME TO `products`")
        }
    }

    //migration after adding updated_at and added_by columns to products table
    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add the new columns to the existing table
            database.execSQL("ALTER TABLE `products` ADD COLUMN `updated_at` INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE `products` ADD COLUMN `added_by` TEXT NOT NULL DEFAULT ''")
        }
    }

    //migration after altering with updated_at and added_by columns to products table
    private val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create a new table with the correct schema
            database.execSQL(
                """
            CREATE TABLE `products_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `company` TEXT NOT NULL,
                `formulation` TEXT NOT NULL,
                `min_stock` INTEGER NOT NULL,
                `min_measure` INTEGER NOT NULL,
                `quantity_available` INTEGER NOT NULL,
                `buying_price` REAL NOT NULL,
                `retail_selling_price` REAL NOT NULL,
                `wholesale_selling_price` REAL NOT NULL,
                `supplier_name` TEXT NOT NULL,
                `date_added` INTEGER NOT NULL,
                `updated_at` INTEGER,
                `added_by` TEXT NOT NULL,
                `expiry_date` INTEGER NOT NULL,
                `description` TEXT
            )
        """
            )

            // Copy the data from the old table to the new table
            database.execSQL(
                """
            INSERT INTO `products_new` (
                `id`, `name`, `company`, `formulation`, `min_stock`, `min_measure`,
                `quantity_available`, `buying_price`, `retail_selling_price`,
                `wholesale_selling_price`, `supplier_name`, `date_added`,
                `updated_at`, `added_by`, `expiry_date`, `description`
            )
            SELECT
                `id`, `name`, `company`, `formulation`, `min_stock`, `min_measure`,
                `quantity_available`, `buying_price`, `retail_selling_price`,
                `wholesale_selling_price`, `supplier_name`, `date_added`,
                `updated_at`, `added_by`, `expiry_date`, `description`
            FROM `products`
        """
            )

            // Remove the old table
            database.execSQL("DROP TABLE `products`")

            // Rename the new table to the old table name
            database.execSQL("ALTER TABLE `products_new` RENAME TO `products`")
        }
    }

    //migration after adding suppliers table and medicines table
    // Migration to add suppliers and medicines tables
    private val MIGRATION_6_7 = object : Migration(6, 7) { // Replace X and Y with the appropriate
        // version
        // numbers
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create the suppliers table
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `suppliers` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `Phone` TEXT NOT NULL,
                `email` TEXT NOT NULL,
                `medicines` TEXT NOT NULL
            )
            """
            )

            // Create the medicines table
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `medicines` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `company` TEXT NOT NULL
            )
            """
            )
        }
    }

    // Migration after modifying suppliers table
    private val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create a new table with the updated schema
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `suppliers_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `Phone` TEXT NOT NULL,
                `email` TEXT NOT NULL,
                `medicines` TEXT NOT NULL
            )
            """
            )

            // Copy the data from the old table to the new table
            database.execSQL(
                """
            INSERT INTO `suppliers_new` (`id`, `name`, `Phone`, `email`, `medicines`)
            SELECT `id`, `name`, `Phone`, `email`, `medicines`
            FROM `suppliers`
            """
            )

            // Remove the old table
            database.execSQL("DROP TABLE `suppliers`")

            // Rename the new table to the old table name
            database.execSQL("ALTER TABLE `suppliers_new` RENAME TO `suppliers`")
        }
    }

    // Migration after downgrading to version 7
    private val MIGRATION_8_7 = object : Migration(8, 7) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create a new table with the previous schema
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `suppliers_old` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `Phone` TEXT NOT NULL,
                `email` TEXT NOT NULL,
                `medicines` TEXT NOT NULL
            )
            """
            )

            // Copy the data from the current table to the old table
            database.execSQL(
                """
            INSERT INTO `suppliers_old` (`id`, `name`, `Phone`, `email`, `medicines`)
            SELECT `id`, `name`, `Phone`, `email`, `medicines`
            FROM `suppliers`
            """
            )

            // Remove the current table
            database.execSQL("DROP TABLE `suppliers`")

            // Rename the old table to the current table name
            database.execSQL("ALTER TABLE `suppliers_old` RENAME TO `suppliers`")
        }
    }

    //migration after adding the sales table(upgraded from 7 to version 10)
    val MIGRATION_7_10 = object : Migration(7, 10) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `sales` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `items` TEXT NOT NULL,
                `total_price` REAL NOT NULL,
                `expected_amount` REAL NOT NULL,
                `cash` REAL NOT NULL,
                `mpesa` REAL NOT NULL,
                `discount` REAL NOT NULL,
                `credit` REAL NOT NULL,
                `seller` TEXT NOT NULL,
                `date` INTEGER NOT NULL
            )
            """
            )
        }
    }

    //migration after adding services table and services offered table
    val MIGRATION_10_11 = object : Migration(10, 11) {
        override fun migrate(database: SupportSQLiteDatabase) {
            //create service table
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `services` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `description` TEXT NOT NULL,
                `price` REAL NOT NULL
            )
            """
            )

            //create service offered
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `services_offered` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `servitor` TEXT NOT NULL,
                `description` TEXT NOT NULL,
                `cash` REAL NOT NULL,
                `mpesa` REAL NOT NULL,
                `total_price` REAL NOT NULL,
                `expected_amount` REAL NOT NULL,
                `date` INTEGER NOT NULL
            )
            """
            )
        }
    }

    @Provides // Annotates a method that returns a dependency instance (AppDatabase instance)
    @Singleton // Ensure only one instance is created
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app, // Use the Application context
            AppDatabase::class.java,
            "my_pos_database"
        ).addCallback(object :
            RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d(
                    "Database",
                    "Database created"
                )
            }
        }).addMigrations(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5,
            MIGRATION_5_6,
            MIGRATION_6_7,
            MIGRATION_7_8,
            MIGRATION_8_7,
            MIGRATION_7_10,
            MIGRATION_10_11

        )
            .build()
    }

    @Provides // Provide UserDao instance
    fun providesUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides // Provide ProductDao instance
    fun providesProductDao(appDatabase: AppDatabase): ProductDao {
        return appDatabase.productDao()
    }

    @Provides // Provide SupplierDao instance
    fun providesSupplierDao(appDatabase: AppDatabase): SupplierDao {
        return appDatabase.supplierDao()
    }

    @Provides // Provide MedicineDao instance
    fun providesMedicineDao(appDatabase: AppDatabase): MedicineDao {
        return appDatabase.medicineDao()
    }

    @Provides // Provide SalesDao instance
    fun providesSalesDao(appDatabase: AppDatabase): SalesDao {
        return appDatabase.salesDao()
    }

    @Provides //provide serviceDao instance
    fun providesServicesDao(appDatabase: AppDatabase): ServicesDao {
        return appDatabase.servicesDao()
    }

    @Provides // Provide Context instance
    @Singleton // Ensure only one instance is created
    fun provideContext(app: Application): Context {
        return app.applicationContext
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds // Bind the UserRepositoryImplementation to UserRepository
    abstract fun bindUserRepository(
        userRepositoryImplementation: UserRepositoryImplementation
    ): UserRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductRepositoryModule {
    @Binds // Bind the ProductRepositoryImplementation to ProductRepository
    abstract fun bindProductRepository(
        productRepositoryImplementation: ProductRepositoryImplementation
    ): ProductRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SupplierRepositoryModule {
    @Binds // Bind the SupplierRepositoryImplementation to SupplierRepository
    abstract fun bindSupplierRepository(
        supplierRepositoryImplementation: SupplierRepositoryImplementation
    ): SupplierRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class MedicineRepositoryModule {
    @Binds // Bind the MedicineRepositoryImplementation to MedicineRepository
    abstract fun bindMedicineRepository(
        medicineRepositoryImplementation: MedicineRepositoryImplementation
    ): MedicineRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SalesRepositoryModule {
    @Binds // Bind the SalesRepositoryImplementation to SalesRepository
    abstract fun bindSalesRepository(
        salesRepositoryImplementation: SalesRepositoryImplementation
    ): SalesRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ServicesRepositoryModule {
    @Binds //bind the serviceRepositoryImplementation to ServicesRepository
    abstract fun bindServiceRepository(
        serviceRepositoryImplementation: ServiceRepositoryImplementation
    ): ServiceRepository
}
