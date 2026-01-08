package com.vital_self.features.history.data.model

import android.content.Context
import com.google.gson.annotations.SerializedName
import com.vital_self.R
import com.vital_self.core.domain.model.Model
import com.vital_self.core.utils.constants.AppConstants
import com.vital_self.core.utils.constants.DIALOG_TYPE
import com.vital_self.core.utils.constants.VITAL_CATEGORY

// Response for /api/user/scan-history/{id}
data class ScanHistoryByIdResponse(
    val message: String?,
    val status: Boolean?,
    val data: ScanHistoryByIdData?
)

data class ScanHistoryByIdData(
    val user: ScanHistoryUser?,
    val scanHistory: ScanHistoryDetail?,
    val vitals: List<VitalItem>?
)

data class ScanHistoryUser(
    val id: Int?,
    val name: String?,
    val email: String?,
    val age: Int?,
    val height: Double?,
    val heightUnit: String?,
    val weight: Double?,
    val weightUnit: String?,
    val gender: String?,
    val smokerStatus: String?
)

data class ScanHistoryDetail(
    val id: Int?,
    val userId: Int?,
    val heartRate: Int?,
    val heartRateLevel: Int?,
    val breathingRate: Int?,
    val breathingRateLevel: Int?,
    val oxygenSaturation: Int?,
    val sdnn: Int?,
    val sdnnLevel: Int?,
    val stressLevel: Int?,
    val bloodPressureSystolic: Int?,
    val bloodPressureDiastolic: Int?,
    val hemoglobin: Double?,
    val hemoglobinA1c: Double?,
    val diabetesRisk: Int?,
    val hypertensionRisk: Int?,
    val wellnessIndex: Int?,
    val wellnessLevel: String?,
    val prq: Double?,
    val prqLevel: Int?,
    val recoveryAbility: Int?,
    val stressResponse: Int?,
    val snsIndex: Double?,
    val pnsIndex: Double?,
    val lfhf: Double?,
    val sd1: Int?,
    val sd2: Int?,
    val rmssd: Int?,
    val meanRri: Int?,
    val highBloodPressureRisk: Int?,
    val highHemoglobinA1cRisk: Int?,
    val highFastingGlucoseRisk: Int?,
    val highTotalCholesterolRisk: Int?,
    val lowHemoglobinRisk: Int?,
    val ascvdRisk: Double?,
    val heartAge: Int?,
    val ascvdRiskLevel: Int?,
    val cardiacWorkload: Double?,
    val pulsePressure: Int?,
    val meanArterialPressure: Int?,
    val createdAt: String?,
    val updatedAt: String?
)

data class VitalItem(
    val fieldName: String?,
    val value: Any?,
    val title: String?,
    val code: String?,
    val group: String?,
    val unit: String?,
    val shortDesc: String?,
    val description: String?,
    val level: String?,
    val confidenceLevel: String?
)

// Extension function to convert API vital to Model.VitalsData
fun VitalItem.toVitalsData(context: Context): Model.VitalsData {
    val vitalIcon = getVitalIcon(code)
    val vitalStatus = getLevelStatus(level)
    val dialogType = getDialogType(code)
    val category = mapGroupToCategory(group)
    val emojiStatus = getEmojiStatus(level)
    val numberOfStates = getNumberOfStates(code)
    val range = getRange(code)

    // Format value based on type
    val formattedValue = when (value) {
        is Number -> {
            if (value.toDouble() == value.toInt().toDouble()) {
                value.toInt().toString()
            } else {
                String.format("%.1f", value.toDouble())
            }
        }
        else -> value?.toString() ?: "0"
    }

    return Model.VitalsData(
        vitalName = mapCodeToVitalName(code),
        vitalValue = formattedValue,
        vitalUnit = if (unit.isNullOrBlank()) null else unit,
        vitalIcon = vitalIcon,
        vitalStatus = vitalStatus,
        vitalDetail = description ?: shortDesc ?: "",
        confidenceLevel = confidenceLevel,
        dialogType = dialogType,
        category = category,
        no_of_state = numberOfStates,
        range = range,
        emojiStatus = emojiStatus
    )
}

private fun getVitalIcon(code: String?): Int {
    return when (code) {
        "heart_rate" -> R.drawable.ic_vital_scanning_heart_rate
        "breathing_rate" -> R.drawable.ic_vital_scanning_breathing_rate
        "prq" -> R.drawable.ic_vital_scanning_prq
        "oxygen_saturation" -> R.drawable.ic_vital_oxygen_saturation_scanning_gray
        "blood_pressure" -> R.drawable.ic_vital_blood_pressure_scanning
        "stress_level" -> R.drawable.ic_vital_scanning_stress_level
        "recovery_ability" -> R.drawable.ic_vital_scanning_recovery_ability
        "stress_response" -> R.drawable.ic_vital_scanning_strss_response
        "hrv_sdnn" -> R.drawable.ic_vital_scanning_hrv
        "Hemoglobin", "hemoglobin" -> R.drawable.ic_vital_scanning_hemoglobin
        "hemoglobin_a1c" -> R.drawable.ic_vital_scanning_hemoglobin
        "high_hemoglobon_a1c_risk" -> R.drawable.ic_vital_scanning_wellness_score
        "high_blood_pressure_risk" -> R.drawable.ic_vital_blood_pressure_scanning
        "ascvd_risk" -> R.drawable.ic_vital_scanning_heart_rate
        "heart_age" -> R.drawable.ic_vital_scanning_prq
        "high_fasting_glucose_risk" -> R.drawable.ic_vital_scanning_hba1a
        "high_total_cholesterol_risk" -> R.drawable.ic_vital_scanning_hemoglobin
        "low_hemoglobin_risk" -> R.drawable.ic_vital_scanning_hemoglobin
        "mean_rri" -> R.drawable.ic_vital_mean_rri
        "pns_index" -> R.drawable.ic_vital_pns
        "sns_index" -> R.drawable.ic_vital_sns
        "rmssd" -> R.drawable.ic_vital_rmssd
        "sd1" -> R.drawable.ic_vital_sd1
        "sd2" -> R.drawable.ic_vital_sd2
        "lf_hf", "lfhf" -> R.drawable.ic_vital_lf_hf
        else -> R.drawable.ic_vital_scanning_heart_rate
    }
}

private fun getLevelStatus(level: String?): Int {
    return when (level?.uppercase()) {
        "NORMAL" -> AppConstants.NORMAL
        "LOW" -> AppConstants.LOW
        "HIGH" -> AppConstants.HIGH
        "MEDIUM" -> AppConstants.MEDIUM
        else -> AppConstants.UNKNOWN
    }
}

private fun getEmojiStatus(level: String?): Int {
    return when (level?.uppercase()) {
        "NORMAL" -> AppConstants.GOOD
        "LOW" -> AppConstants.NOT_GOOD
        "HIGH" -> AppConstants.BAD
        "MEDIUM" -> AppConstants.NOT_GOOD
        else -> AppConstants.UNKNOWN
    }
}

private fun getDialogType(code: String?): String {
    return when (code) {
        "heart_rate", "breathing_rate", "prq", "blood_pressure", "oxygen_saturation" -> DIALOG_TYPE.RANGE.name
        "stress_level", "recovery_ability", "stress_response", "hrv_sdnn",
        "high_hemoglobon_a1c_risk", "high_blood_pressure_risk",
        "high_fasting_glucose_risk", "high_total_cholesterol_risk", "low_hemoglobin_risk" -> DIALOG_TYPE.STATE.name
        else -> DIALOG_TYPE.BASIC.name
    }
}

private fun mapGroupToCategory(group: String?): String {
    return when (group) {
        "VITAL_SIGNS" -> VITAL_CATEGORY.VITAL_SIGN.name
        "BLOOD" -> VITAL_CATEGORY.BLOOD.name
        "BLOOD_TEST" -> VITAL_CATEGORY.BLOOD_TEST.name
        "STRESS_LEVEL" -> VITAL_CATEGORY.STRESS.name
        "ENERGY" -> VITAL_CATEGORY.ENERGY.name
        "HEART_RATE_VARIABILITY" -> VITAL_CATEGORY.HRV.name
        "HYPERTENSION_RISK", "DIABETIC_RISK", "ASCVD_RISK", "RISK" -> VITAL_CATEGORY.RISK.name
        "NOT_SHOW_VITAL_SIGNS" -> VITAL_CATEGORY.RISK.name
        else -> VITAL_CATEGORY.VITAL_SIGN.name
    }
}

private fun mapCodeToVitalName(code: String?): String {
    return when (code) {
        "heart_rate" -> AppConstants.HEART_RATE
        "breathing_rate" -> AppConstants.BREATHING_RATE
        "prq" -> AppConstants.PRQ
        "oxygen_saturation" -> AppConstants.OXYGEN_SATURATION
        "blood_pressure" -> AppConstants.BLOOD_PRESSURE
        "stress_level" -> AppConstants.STRESS_LEVEL
        "recovery_ability" -> AppConstants.RECOVERY_ABILITY
        "stress_response" -> AppConstants.STRESS_RESPONSE
        "hrv_sdnn" -> AppConstants.HRV_SDNN
        "Hemoglobin", "hemoglobin" -> AppConstants.HEMOGLOBIN
        "hemoglobin_a1c" -> AppConstants.HEMOGLOBIN_A1C
        "high_hemoglobon_a1c_risk" -> AppConstants.HIGH_BLOOD_PRESSURE_RISK
        "high_blood_pressure_risk" -> AppConstants.HIGH_HEMOGLOBIN_A1C_RISK
        "ascvd_risk" -> AppConstants.ASCVD
        "heart_age" -> AppConstants.HEART_AGE
        "high_fasting_glucose_risk" -> AppConstants.HIGH_FIASTING_GLUCOSE
        "high_total_cholesterol_risk" -> AppConstants.TOTAL_COLESTROL
        "low_hemoglobin_risk" -> AppConstants.LOW_HEMOGLOBIN_RISK
        "mean_rri" -> AppConstants.MEAN_RRI
        "pns_index" -> AppConstants.PNS
        "sns_index" -> AppConstants.SNS
        "rmssd" -> AppConstants.RMSSD
        "sd1" -> AppConstants.SD1
        "sd2" -> AppConstants.SD2
        "lf_hf", "lfhf" -> AppConstants.LFHF
        else -> code ?: ""
    }
}

private fun getNumberOfStates(code: String?): Int {
    return when (code) {
        "stress_level", "recovery_ability", "stress_response",
        "high_hemoglobon_a1c_risk", "high_blood_pressure_risk", "high_total_cholesterol_risk" -> 3
        "high_fasting_glucose_risk", "low_hemoglobin_risk" -> 2
        else -> 0
    }
}

private fun getRange(code: String?): ArrayList<String> {
    return when (code) {
        "heart_rate" -> arrayListOf("40", "60", "100", "240")
        "breathing_rate" -> arrayListOf("6", "12", "20", "40")
        "prq" -> arrayListOf("", "4", "5", "")
        "blood_pressure" -> arrayListOf("systolic range", "100", "130", "")
        "oxygen_saturation" -> arrayListOf("0", "95", "100", "")
        "mean_rri" -> arrayListOf("", "600", "1000", "")
        "hrv_sdnn" -> arrayListOf("", "50", "", "")
        "sns_index" -> arrayListOf("", "-1", "1", "")
        "rmssd" -> arrayListOf("", "25", "43", "")
        else -> arrayListOf()
    }
}

// Convert list of VitalItems to ArrayList of VitalsData
fun List<VitalItem>.toVitalsDataList(context: Context): ArrayList<Model.VitalsData> {
    val result = ArrayList<Model.VitalsData>()

    // Group blood pressure items together
    val bpSystolic = this.find { it.fieldName == "bloodPressureSystolic" }
    val bpDiastolic = this.find { it.fieldName == "bloodPressureDiastolic" }

    for (item in this) {
        // Skip diastolic as we combine it with systolic
        if (item.fieldName == "bloodPressureDiastolic") continue

        // Handle blood pressure specially - combine systolic and diastolic
        if (item.fieldName == "bloodPressureSystolic" && bpDiastolic != null) {
            val systolicValue = (item.value as? Number)?.toInt() ?: 0
            val diastolicValue = (bpDiastolic.value as? Number)?.toInt() ?: 0

            result.add(Model.VitalsData(
                vitalName = AppConstants.BLOOD_PRESSURE,
                vitalValue = "$systolicValue/$diastolicValue",
                vitalUnit = AppConstants.BLOOD_PRESSURE_UNIT,
                vitalIcon = R.drawable.ic_vital_blood_pressure_scanning,
                vitalStatus = getLevelStatus(item.level),
                vitalDetail = item.description ?: item.shortDesc ?: "",
                confidenceLevel = item.confidenceLevel,
                dialogType = DIALOG_TYPE.RANGE.name,
                category = VITAL_CATEGORY.BLOOD.name,
                range = arrayListOf("systolic range", "100", "130", ""),
                emojiStatus = getEmojiStatus(item.level)
            ))
        } else {
            // Show all vitals from API response
            result.add(item.toVitalsData(context))
        }
    }

    return result
}
