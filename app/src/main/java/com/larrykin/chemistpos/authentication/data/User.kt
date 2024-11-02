package com.larrykin.chemistpos.authentication.data


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    val phoneNumber: String,
    @ColumnInfo(name = "chemist_name")
    val chemistName: String,
    @ColumnInfo(name = "role")
    val role: Role,
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),
    @ColumnInfo(name = "profile_Picture_url")
    var profilePictureUrl: String? = null,
    @ColumnInfo(name = "timestamp") var timestamp: Long = System.currentTimeMillis()
) {
    // No-argument constructor for Firestore
    constructor() : this(
        email = "",
        username = "",
        password = "",
        phoneNumber = "",
        chemistName = "",
        role = Role.USER,
        createdAt = Date(),
        profilePictureUrl = null,
        timestamp = System.currentTimeMillis()
    )
}
