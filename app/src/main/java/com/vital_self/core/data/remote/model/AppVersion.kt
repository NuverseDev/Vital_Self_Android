package com.vital_self.core.data.remote.model

import com.google.gson.annotations.SerializedName

data class CheckVersionRequest(
    @SerializedName("app_type")
    val platform: String,
    @SerializedName("version")
    val app_version: String,
    @SerializedName("app_name")
    val app_name: String
)

data class CheckVersionResponse(
    @SerializedName("status")
    val success: Boolean,
    @SerializedName("message")
    val message: Boolean,
    @SerializedName("data")
    val data: AppVersion
)

data class AppVersion(
    @SerializedName("isMatch")
    val isMatch: Boolean,
    @SerializedName("deviceVersion")
    val deviceVersion: DeviceVersion
)

data class DeviceVersion(
    @SerializedName("id")
    val id: Int,
    @SerializedName("platform")
    val platform: String,
    @SerializedName("version")
    val version: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)

