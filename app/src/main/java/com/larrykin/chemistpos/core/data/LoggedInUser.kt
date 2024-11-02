package com.larrykin.chemistpos.core.data

import android.os.Parcelable
import com.larrykin.chemistpos.authentication.data.Role
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class LoggedInUser(
    val username: String,
    val email: String,
    val role: Role,
    val chemistName: String,
    val phoneNumber: String,
    val createdAt: Date
) : Parcelable
