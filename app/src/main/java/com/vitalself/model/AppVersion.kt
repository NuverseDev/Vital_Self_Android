package com.vitalself.model

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
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("version_match")
    val version_match: Boolean,
    @SerializedName("current_version")
    val current_version: String
)