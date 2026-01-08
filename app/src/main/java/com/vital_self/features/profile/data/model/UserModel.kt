package com.vital_self.features.profile.data.model

import com.google.gson.annotations.SerializedName

data class UserLoginRequest(
    val userName : String,
    val password : String
)
data class UserRequest(
    val userId : Int
)

data class UserRegisterRequest(
    val userName : String,
    val email : String,
    val password : String
)

data class GetUserResponse(
    val message : String,
    val data : UserModel
)

data class UpdateScanResponse(
    val message: String,
    val user : UserModel
)
data class UserModel(
    @SerializedName("id")
    val userId : Int,
    val licenseKey : String,
    @SerializedName("userId")
    val id : Int,
    val userKey: String,
    val status: Boolean?,
    val availableScan: Int?,
    val totalScan: Int?,
    val created_at: String?,
    val updatedAt : String?
)

data class userSettings(
    val time : Int,
    val autoMeasurement : Boolean? = false,
    val qrCodeReport : Boolean? = false,
    val objectDetection : Boolean? = false,
    val customReportId : Int? = 0,
    val customScanScreen : Int? = 0
)

