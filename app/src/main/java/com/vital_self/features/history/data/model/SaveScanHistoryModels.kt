package com.vital_self.features.history.data.model

import com.google.gson.annotations.SerializedName

// Request body for POST /api/user/scan-history
data class SaveScanHistoryRequest(
    @SerializedName("heartRate")
    val heartRate: Int?,
    @SerializedName("heartRateLevel")
    val heartRateLevel: Int?,
    @SerializedName("breathingRate")
    val breathingRate: Int?,
    @SerializedName("breathingRateLevel")
    val breathingRateLevel: Int?,
    @SerializedName("oxygenSaturation")
    val oxygenSaturation: Int?,
    @SerializedName("sdnn")
    val sdnn: Int?,
    @SerializedName("sdnnLevel")
    val sdnnLevel: Int?,
    @SerializedName("stressLevel")
    val stressLevel: Int?,
    @SerializedName("bloodPressureSystolic")
    val bloodPressureSystolic: Int?,
    @SerializedName("bloodPressureDiastolic")
    val bloodPressureDiastolic: Int?,
    @SerializedName("hemoglobin")
    val hemoglobin: Double?,
    @SerializedName("hemoglobinA1c")
    val hemoglobinA1c: Double?,
    @SerializedName("diabetesRisk")
    val diabetesRisk: Int?,
    @SerializedName("hypertensionRisk")
    val hypertensionRisk: Int?,
    @SerializedName("wellnessIndex")
    val wellnessIndex: Int?,
    @SerializedName("wellnessLevel")
    val wellnessLevel: String?,
    @SerializedName("prq")
    val prq: Double?,
    @SerializedName("prqLevel")
    val prqLevel: Int?,
    @SerializedName("recoveryAbility")
    val recoveryAbility: Int?,
    @SerializedName("stressResponse")
    val stressResponse: Int?,
    @SerializedName("snsIndex")
    val snsIndex: Double?,
    @SerializedName("pnsIndex")
    val pnsIndex: Double?,
    @SerializedName("lfhf")
    val lfhf: Double?,
    @SerializedName("sd1")
    val sd1: Int?,
    @SerializedName("sd2")
    val sd2: Int?,
    @SerializedName("rmssd")
    val rmssd: Int?,
    @SerializedName("meanRri")
    val meanRri: Int?,
    @SerializedName("highBloodPressureRisk")
    val highBloodPressureRisk: Int?,
    @SerializedName("highHemoglobinA1cRisk")
    val highHemoglobinA1cRisk: Int?,
    @SerializedName("highFastingGlucoseRisk")
    val highFastingGlucoseRisk: Int?,
    @SerializedName("highTotalCholesterolRisk")
    val highTotalCholesterolRisk: Int?,
    @SerializedName("lowHemoglobinRisk")
    val lowHemoglobinRisk: Int?,
    @SerializedName("ascvdRisk")
    val ascvdRisk: Double?,
    @SerializedName("heartAge")
    val heartAge: Int?,
    @SerializedName("ascvdRiskLevel")
    val ascvdRiskLevel: Double?,
    @SerializedName("cardiacWorkload")
    val cardiacWorkload: Double?,
    @SerializedName("pulsePressure")
    val pulsePressure: Int?,
    @SerializedName("meanArterialPressure")
    val meanArterialPressure: Int?
)

// Response body for POST /api/user/scan-history
// Reuses ScanHistoryByIdData structure since response is identical
data class SaveScanHistoryResponse(
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?,
    @SerializedName("data")
    val data: ScanHistoryByIdData?
)
