package com.vital_self.features.history.data.model

import com.google.gson.annotations.SerializedName

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
