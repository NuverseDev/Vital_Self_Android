package com.vital_self.features.auth.data.model

import com.google.gson.annotations.SerializedName

// Request Models
data class LoginRequest(
    val email: String,
    val password: String
)

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)

data class ForgotPasswordRequest(
    val email: String
)

// Response Models
data class AuthResponse(
    val message: String,
    val status: Boolean,
    val data: AuthData?
)

data class AuthData(
    val accessToken: String,
    val refreshToken: String,
    val user: AuthUser,
    val key: String
)

data class AuthUser(
    val id: Int,
    val name: String,
    val email: String,
    val age: Int? = null,
    val height: Double? = null,
    val heightUnit: String? = null,
    val weight: Double? = null,
    val weightUnit: String? = null,
    val gender: String? = null,
    val smokerStatus: String? = null,
    @SerializedName("freescans")
    val freeScans: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val doctor : Boolean
)

data class LicenseKey(
    val id: Int? = null,
    val key: String? = null,
    val status: Boolean? = null
)

data class ForgotPasswordResponse(
    val message: String,
    val status: Boolean
)

// Profile Update Models
data class ProfileUpdateRequest(
    val name: String? = null,
    val age: Int? = null,
    val height: Double? = null,
    val heightUnit: String? = null,
    val weight: Double? = null,
    val weightUnit: String? = null,
    val gender: String? = null,
    val smokerStatus: String? = null,
    val doctor: Boolean
)

data class ProfileUpdateResponse(
    val message: String,
    val status: Boolean,
    val data: AuthUser?
)
