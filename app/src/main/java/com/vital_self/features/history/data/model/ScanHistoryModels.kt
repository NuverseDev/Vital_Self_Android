package com.vital_self.features.history.data.model

import com.google.gson.annotations.SerializedName
import com.vital_self.features.scan.presentation.scan.MeasurementResult

// Response for /api/user/scan-history-by-date?date=YYYY-MM-DD
data class ScanHistoryByDateResponse(
    val message: String,
    val status: Boolean,
    val data: ScanHistoryData?
)

data class ScanHistoryData(
    @SerializedName("log_date")
    val logDate: String,
    @SerializedName("scan_results")
    val scanResults: List<ScanResultItem>?
)

data class ScanResultItem(
    val id: Int? = null,
    val resultDate: String? = null,
    val resultTime: String? = null,
    val createdDate: String? = null,
    val heartRate: Int? = null,
    val breathingRate: Int? = null,
    val respiration: Int? = null,
    val oxygenSaturation: Int? = null,
    val bloodPressure: String? = null,
    val bloodPressureSystolic: Int? = null,
    val bloodPressureDiastolic: Int? = null,
    val stressLevel: Int? = null,
    val stressResponse: Int? = null,
    val recoveryAbility: Int? = null,
    val hrvSdnn: Int? = null,
    val prq: Double? = null,
    val hemoglobin: Double? = null,
    val hba1c: Double? = null,
    val diabeticRisk: Int? = null,
    val hypertensionRisk: Int? = null,
    val wellnessScore: Int? = null,
    val wellnessLevel: String? = null,
    val highBloodPressureRisk: Int? = null,
    val highHemoglobinA1cRisk: Int? = null,
    val highFastingGlucoseRisk: Int? = null,
    val highTotalCholesterolRisk: Int? = null,
    val lowHemoglobinRisk: Int? = null,
    val ascvdRisk: Double? = null,
    val heartAge: Int? = null
)

// Response for /api/user/calendar?month=MM&year=YYYY
data class CalendarResponse(
    val message: String,
    val status: Boolean,
    val data: CalendarData?
)

data class CalendarData(
    val list: List<CalendarDayItem>?
)

data class CalendarDayItem(
    val date: String,
    val score: Int
)

// Extension function to convert API response to MeasurementResult
fun ScanResultItem.toMeasurementResult(): MeasurementResult {
    return MeasurementResult(
        date = resultDate ?: "",
        time = resultTime ?: "",
        name = "",
        gender = "",
        age = "",
        height = "",
        weight = "",
        heartRate = heartRate?.toString() ?: "0",
        breathingRate = breathingRate?.toString() ?: "0",
        PRQ = prq?.toString() ?: "0",
        hrv_sdnn = hrvSdnn?.toString() ?: "0",
        hypertensionRisk = hypertensionRisk?.toString() ?: "0",
        diabeticRisk = diabeticRisk?.toString() ?: "0",
        ascvd = ascvdRisk?.toString() ?: "0",
        highFastingGlucose = highFastingGlucoseRisk?.toString() ?: "0",
        lowHemoglobinRisk = lowHemoglobinRisk?.toString() ?: "0",
        heartAge = heartAge?.toString() ?: "0",
        totalColestrol = highTotalCholesterolRisk?.toString() ?: "0",
        oxygenSat = oxygenSaturation?.toString() ?: "0",
        bloodPressureSystolic = bloodPressureSystolic?.toString() ?: "0",
        bloodPressureDistolic = bloodPressureDiastolic?.toString() ?: "0",
        hemoglobin = hemoglobin?.toString() ?: "0",
        hemoglobinA1c = hba1c?.toString() ?: "0",
        stressLevel = stressLevel?.toString() ?: "0",
        meanRri = 0,
        pnsIndex = 0.0,
        snsIndex = 0.0,
        RMMSD = 0,
        sd1 = 0,
        sd2 = 0,
        lfhf = 0.0,
        recoveryRate = recoveryAbility?.toString() ?: "0",
        stressResp = stressResponse?.toString() ?: "0",
        wellnessLevel = wellnessLevel ?: "",
        wellnessIndex = wellnessScore?.toString() ?: "0",
        heartRateConfi = null,
        breathingRateConfid = null,
        prqConfi = null,
        sdnCofi = null
    )
}
