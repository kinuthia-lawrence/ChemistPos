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
import com.larrykin.chemistpos.home.data.ProductRepositoryImplementation
import com.larrykin.chemistpos.home.domain.ProductRepository
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
        }).addMigrations(MIGRATION_4_5)
            .build()
    }

    @Provides // Provide UserDao instance
    fun providesUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }
    @Provides // Provide ProductDao instance
    fun providesProductDao(appDatabase: AppDatabase): com.larrykin.chemistpos.home.data.ProductDao {
        return appDatabase.productDao()
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
