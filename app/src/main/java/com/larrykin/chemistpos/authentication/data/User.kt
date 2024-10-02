package com.larrykin.chemistpos.authentication.data


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

//this file contains the data classes that represent the tables in the database, just @entity in springboot
@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true)
    ]
)
data class User(
    @ColumnInfo(name = "email")
    @PrimaryKey val email: String,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "password")
    val password: String,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: Number,
    @ColumnInfo(name = "chemist_name")
    val chemistName: String,
    @ColumnInfo(name = "role")
    val role: Role,
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),
)
