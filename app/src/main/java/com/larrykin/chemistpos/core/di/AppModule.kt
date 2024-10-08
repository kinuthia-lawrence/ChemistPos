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
        }).addMigrations(MIGRATION_2_3)
            .build()
    }

    @Provides // Provide UserDao instance
    fun providesUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
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
