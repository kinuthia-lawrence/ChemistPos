package com.larrykin.chemistpos.core.di

import android.content.Context
import androidx.room.Room
import com.larrykin.chemistpos.authentication.data.UserDao
import com.larrykin.chemistpos.core.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module //annotates classes that define how dependencies are provided to the system
@InstallIn(SingletonComponent::class) //installing AppModule to component( ex.applicationComponent)
object AppModule {
    @Provides // annotates a method that returns a dependency instance( this case AppDatabase instance)
    @Singleton//ensure only one instance is created
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,//check contrast with applicationContext
            AppDatabase::class.java,
            "my_pos_database"
        ).build()
    }

    @Provides //Provide userDao instance
    fun providesUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }
}